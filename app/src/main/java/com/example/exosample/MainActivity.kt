package com.example.exosample

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.text.format.Time
import android.view.View
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottomsheet.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var player: SimpleExoPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var songName: Array<String> = arrayOf("Blank Space", "Naina Lade", "Luka Chhupi", "Thousand Years")
        var index:Int=0
        player = ExoPlayerFactory.newSimpleInstance(
            this@MainActivity,
            DefaultTrackSelector(),
            DefaultLoadControl()
        )
            initializePlayer()
        startSongProgress()


        next.setOnClickListener(View.OnClickListener {
            player.next()
            if(index<songName.size){
                if(index==songName.size-1){
                    index=0;
                }else{
                    index++;
                }
                songTitle.text=songName.get(index)
            }
        })

        previous.setOnClickListener(View.OnClickListener {
            player.previous()
            player.previous()

            if(index>=0){
                if(index==0){
                    index=songName.size-1
                }else{
                    index--;
                }

                songTitle .text=songName.get(index)
            }
        })



        pausebutton.setOnClickListener(View.OnClickListener {

            player.setPlayWhenReady(false);
            player.getPlaybackState();

        })

        playbutton.setOnClickListener(View.OnClickListener {

            player.setPlayWhenReady(true);
            player.getPlaybackState();
        })

        sharePlaylist.setOnClickListener {
            var mBottomSheetDialog:BottomSheetDialog = BottomSheetDialog(this)
            val sheetView :View = this.layoutInflater.inflate(R.layout.bottomsheet,null)
            mBottomSheetDialog.setContentView(sheetView)
            mBottomSheetDialog.show()

            mBottomSheetDialog.whatsApp.setOnClickListener(View.OnClickListener {
                shareWhatsapp()
            })

            mBottomSheetDialog.gmail.setOnClickListener(View.OnClickListener {
                shareGmail()
            })

            mBottomSheetDialog.message.setOnClickListener(View.OnClickListener {
                shareMessage()
            })
        }

    }



    fun initializePlayer() {
        var bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter();

        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory();

        val trackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter);


        var dataSource: DataSource.Factory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, getPackageName()),
            DefaultBandwidthMeter()
        );



        var firstsource:MediaSource = ProgressiveMediaSource.Factory(dataSource)
            .createMediaSource(
                RawResourceDataSource
                    .buildRawResourceUri(R.raw.vande))

        var secondsource:MediaSource = ProgressiveMediaSource.Factory(dataSource)
            .createMediaSource(RawResourceDataSource
                .buildRawResourceUri(R.raw.naina_lade))

        var thirdsource:MediaSource = ProgressiveMediaSource.Factory(dataSource)
            .createMediaSource(RawResourceDataSource
                .buildRawResourceUri(R.raw.luka_chhupi))

        var forthsource:MediaSource = ProgressiveMediaSource.Factory(dataSource)
            .createMediaSource(RawResourceDataSource
                .buildRawResourceUri(R.raw.blankspace))

        var concatenatingMediaSource: ConcatenatingMediaSource = ConcatenatingMediaSource(firstsource,secondsource,thirdsource,forthsource)

//        val dataSpec: DataSpec = DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.blankspace))
//
        val rawResourceDataSource: RawResourceDataSource = RawResourceDataSource(this@MainActivity)
//
//        rawResourceDataSource.open(dataSpec)


        var audiosource: MediaSource = ExtractorMediaSource(rawResourceDataSource.uri,dataSource
            ,extractorsFactory,null,null)

        var loopingMediaSource: LoopingMediaSource = LoopingMediaSource(concatenatingMediaSource)

        player.prepare(loopingMediaSource)

        player.setPlayWhenReady(true)
    }

    private fun startSongProgress() {
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                val currentProgress = (player.currentPosition * 100 / player.duration).toInt()
                progress_bar.progress = currentProgress

                progressbar.progress = currentProgress
//                player.seekTo((currentProgress*1000).toLong())

                val timeLine = player.currentPosition
                val ctoMinutes = TimeUnit.MILLISECONDS.toMinutes(timeLine)
                val ctoSeconds = TimeUnit.MILLISECONDS.toSeconds(timeLine)
                var second = ctoSeconds.toString().take(2)
                var minut = ctoMinutes.toString()
                if (minut.length == 1) {
                    minut = "0$minut"
                }
                if (second.length == 1) {
                    second = "0$second"
                }
                currentduration.text = "$minut:$second"
                handler.postDelayed(this, 1000)


                totalduration.text=player.duration.toString()
                val tdtimeline = player.duration
                val tduMinute=TimeUnit.MILLISECONDS.toMinutes(tdtimeline).toString()
                val tduSecond=(TimeUnit.MILLISECONDS.toSeconds(tdtimeline) % 60).toString()
                if (tduMinute.length == 1) {
                    minut = "0$tduMinute"
                }
                if (tduSecond.length == 1) {
                    second = "0$tduSecond"
                }
                totalduration.text="$minut:$second"
//                player.seekTo(progress_bar.progress.toLong())


                /*mPlayer.seekTo(progress);
            seekBar.setProgress(progress);*/
//

            }
        }
        handler.postDelayed(runnable, 0)

}

    fun shareWhatsapp(){

        val sendIntent=Intent()
        sendIntent.action = Intent.ACTION_SEND
        val path = "android.resource:/$packageName/${R.raw.blankspace}"
        sendIntent.putExtra(Intent.EXTRA_TEXT, path)
       sendIntent.type = "text/plain"
        sendIntent.setPackage("com.whatsapp")
        startActivity(sendIntent)

    }

    fun shareGmail(){
        val sendIntent=Intent()
        sendIntent.action = Intent.ACTION_SEND
        val path = "android.resource:/$packageName/${R.raw.blankspace}"
        sendIntent.putExtra(Intent.EXTRA_TEXT, path)
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.google.android.gm")
        startActivity(sendIntent)

    }

    private fun shareMessage() {
        val smsIntent =
            Intent(Intent.ACTION_VIEW)
        smsIntent.type = "vnd.android-dir/mms-sms"
        val path = "android.resource:/$packageName/${R.raw.blankspace}"
//        smsIntent.putExtra("address", 7044326434)
        smsIntent.putExtra("sms_body", path)
        startActivity(smsIntent)
    }

    }
