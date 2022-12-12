import sun.*
import java.applet.*
import java.awt.FlowLayout
import java.awt.Point
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.*
import java.util.*
import java.util.Timer
import javax.imageio.ImageIO
import javax.sound.sampled.*
import javax.swing.*
import kotlin.concurrent.scheduleAtFixedRate
// artifact "IdiotRolld_jar" is where this program is built to as a jar file. | out/artifacts/IdiotRolld_jar/IdiotRoll.jar
/*
*          you are rickroll'd﹗
*
*             ☺  ☺  ☺
*/
fun main(args: Array<String>) { // ☻ ☺
    SimpleAudioPlayer().play() // ♩ Never gonna give you up ♫
    DisplayImage() // rick
}

// Exactly what it sounds like, it is an audio player.
class SimpleAudioPlayer { // *most* of this class is copy/pasted from the internet and translated from Java into Kotlin. source: https://www.geeksforgeeks.org/play-audio-file-using-java/
    // to store current position
    var currentFrame: Long? = null
    var clip: Clip

    // current status of clip
    var status: String? = null
    var audioInputStream: AudioInputStream

    // constructor to initialize streams and clip
    init {
        // create AudioInputStream object
        try {
        audioInputStream = AudioSystem.getAudioInputStream(filePath.let { File(it).absoluteFile }) // the .let was leftover from an attempted fix from a null pointer exception, and I never bothered to change it back. Don't remember exactly what it was before lol
        } catch (e: Exception) {
            println(e)
            println("the path is: $filePath") // before a bug fix, running the application with Gradle from the Intellij IDEA IDE would work, but running a compiled artifact jar file would either return an empty path or have access denied.
        }

        audioInputStream = AudioSystem.getAudioInputStream(filePath.let { File(it).absoluteFile }) // won't work if I don't this here, which means the code above will not actually prevent an exception from crashing the program...

        // create clip reference
        clip = AudioSystem.getClip()

        // open audioInputStream to the clip
        clip.open(audioInputStream)
        clip.loop(Clip.LOOP_CONTINUOUSLY)
    }

    // Work as the user enters his choice
    @kotlin.Throws(IOException::class, LineUnavailableException::class, UnsupportedAudioFileException::class)
    private fun gotoChoice(c: Int) {
        when (c) {
            1 -> pause()
            2 -> resumeAudio()
            3 -> restart()
            4 -> stop()
            5 -> {
                println(
                    "Enter time (" + 0 +
                            ", " + clip.microsecondLength + ")"
                )
                val sc = Scanner(System.`in`)
                val c1: Long = sc.nextLong()
                jump(c1)
            }
        }
    }

    // Method to play the audio
    fun play() {
        //start the clip
        clip.start()
        status = "play"
    }

    // Method to pause the audio
    fun pause() {
        if(status!! == "paused") {
            println("audio is already paused")
            return
        }
        currentFrame = clip.microsecondPosition
        clip.stop()
        status = "paused"
    }

    // Method to resume the audio
    @kotlin.Throws(UnsupportedAudioFileException::class, IOException::class, LineUnavailableException::class)
    fun resumeAudio() {
        if(status!! == "play") {
            println(
                "Audio is already " +
                        "being played"
            )
            return
        }
        clip.close()
        resetAudioStream()
        currentFrame?.let { clip.microsecondPosition = it }
        play()
    }

    // Method to restart the audio
    @kotlin.Throws(IOException::class, LineUnavailableException::class, UnsupportedAudioFileException::class)
    fun restart() {
        clip.stop()
        clip.close()
        resetAudioStream()
        currentFrame = 0L
        clip.microsecondPosition = 0
        play()
    }

    // Method to stop the audio
    @kotlin.Throws(UnsupportedAudioFileException::class, IOException::class, LineUnavailableException::class)
    fun stop() {
        currentFrame = 0L
        clip.stop()
        clip.close()
    }

    // Method to jump over a specific part
    @kotlin.Throws(UnsupportedAudioFileException::class, IOException::class, LineUnavailableException::class)
    fun jump(c: Long) {
        if(c > 0 && c < clip.microsecondLength) {
            clip.stop()
            clip.close()
            resetAudioStream()
            currentFrame = c
            clip.microsecondPosition = c
            play()
        }
    }

    // Method to reset audio stream
    @kotlin.Throws(UnsupportedAudioFileException::class, IOException::class, LineUnavailableException::class)
    fun resetAudioStream() {
        audioInputStream = AudioSystem.getAudioInputStream(
            File(filePath).absoluteFile
        )
        clip.open(audioInputStream)
        clip.loop(Clip.LOOP_CONTINUOUSLY)
    }

    companion object {
        var filePath: String = "${DisplayImage().getPath().removeSuffix("IdiotRoll.jar")}res\\roll.wav"
        fun main(args: Array<String?>?) {
            try {
                filePath = this::class.java.getResource("/roll.wav")?.path ?: ""
                val audioPlayer = SimpleAudioPlayer()
                audioPlayer.play()
                val sc = Scanner(System.`in`)
                while (true) {
                    println("1. pause")
                    println("2. resume")
                    println("3. restart")
                    println("4. stop")
                    println("5. Jump to specific time")
                    val c: Int = sc.nextInt()
                    audioPlayer.gotoChoice(c)
                    if(c == 4) break
                }
                sc.close()
            } catch (ex: Exception) {
                println("Error with playing sound.")
                ex.printStackTrace()
            }
        }
    }
}

class DisplayImage { // The base code of this class was mostly taken from StackOverflow, translated to Kotlin, and adapted/expanded on for this program.

    fun getPath(): String { // get working directory of the program. Used to find needed files located in the same folder as the jar file.
        return File(this.javaClass.protectionDomain.codeSource.location.toURI()).path
    // Note that I later had to remove "\IdiotRoll.jar" from the path in order to get to file in the same folder instead of inside the jar, since if the files were inside the jar, access to the files would be denied and/or their path would return null.
    // This is why you should NOT rename the jar file unless you also do so in the code, nor should you move the jar out of its folder unless you move the "res" folder with it. Instead, create a shortcut to the jar, which can then be renamed and/or moved to your desktop.
    }

    init { // this is messy because I had to experiment with things to get it working right again and never bothered to remove things thing I could swear used to work or could come in handy later
//        println("path is: ${getPath()}") // Used for debugging
        val screenWidth = Toolkit.getDefaultToolkit().screenSize.width // gets the size of the screen
        val screenHeight = Toolkit.getDefaultToolkit().screenSize.height
        val rick = "${getPath().removeSuffix("IdiotRoll.jar")}res\\roll.gif"//this.javaClass.getResourceAsStream("/1.jpg")// getResource("/1.jpg")?.path ?: "" // <- past versions of this code that used to work in other conditions.
//        val white = "${getPath().removeSuffix("IdiotRoll.jar")}res\\2.jpg"//this.javaClass.getResourceAsStream("/2.jpg")//?.path ?: "" //<- this would have worked when running via Gradle with the IDE but not with a compiled jar file. the current code works with the jar file assuming you arrange the files correctly as I have, and don't rename the files.
//        val cldr = this.javaClass.classLoader // I think this does relatively the same thing as my getPath() function. I added this and the fallowing 3 lines when making the rickroll version so I could animate the GIF. Its kinda redundant tho but I'm too lazy to just combine it rn lol
//        val imageURL = cldr.getResource("roll.gif") // this would replace value rick if it wasn't in a jar, ie running via Gradle from the IDE instead of with a compiled jar.  There would be a few other things that I left commented out that would replace the current code in that scenario too.
        val img = ImageIO.read(File(rick)) // the gif
        val imageIcon = ImageIcon(rick)
        val iconLabel = JLabel()
        iconLabel.icon = imageIcon
        imageIcon.imageObserver = iconLabel // animates the GIF
//        val img2 = ImageIO.read(File(white))
//        var icon = ImageIcon(img)
        val frame = JFrame()
        frame.layout = FlowLayout()
        frame.setSize(img.width, img.height) // makes the window the same size as the gif so that the gif fills the window perfectly
//        val lbl = JLabel()
//        lbl.icon = icon
        frame.add(iconLabel) // iconLabel for the GIF, lbl for idiot ☺☺☺
        frame.location = Point((0..screenWidth-frame.width).random(), (0..screenHeight-frame.height).random())
        frame.isVisible = true
        frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE

        frame.addWindowListener(object : WindowAdapter() {
        override fun windowClosing(e: WindowEvent?) { // user summons 4 more windows if they try to close one window. If they click close all windows when there's 5 one the screen, they're in for a bad time. (20 more windows, 25 total)
            main(emptyArray())
            main(emptyArray())
            main(emptyArray())
            main(emptyArray())
        }
    })

        val speed = (16..48).random() // different moving speed for every window
        var moveRight = Random().nextBoolean()
        var moveUp = moveRight

    Timer().scheduleAtFixedRate(30, 26) {
        var x = if(moveRight) speed else -speed
        var y = if(moveUp) speed else -speed
        var newLocation = Point((frame.x + x), (frame.y + y))
        if(newLocation.x !in(1 until (screenWidth - frame.width))) {
            moveRight = !moveRight
            x = if(moveRight) speed else -speed
            if((1..1150).random() == 30) main(emptyArray())

        }
        if(newLocation.y !in(1 until (screenHeight - frame.height))) {
            moveUp = !moveUp
            y = if(moveUp) speed else -speed
            if((1..30).random() == 12) main(emptyArray())
        }
        newLocation = Point((frame.x + x), (frame.y + y))
        frame.location = newLocation
    }

    }

    companion object {
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            DisplayImage()
            SimpleAudioPlayer().play()
        }
    }
}

/*
╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╭╮╱╱╱╱╱╱╱╭╮╱╱
╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱┃┃╱╱╱╱╱╱╭╯╰╮╱
╭╮╱╭╮╭━━╮╭╮╭╮╱╭━━╮╭━╮╭━━╮╱╭━━╮╭━╮╱╱╭╮╭━╯┃╭╮╭━━╮╰╮╭╯╱
┃┃╱┃┃┃╭╮┃┃┃┃┃╱┃╭╮┃┃╭╯┃┃━┫╱┃╭╮┃┃╭╮╮╱┣┫┃╭╮┃┣┫┃╭╮┃╱┃┃╱╱
┃╰━╯┃┃╰╯┃┃╰╯┃╱┃╭╮┃┃┃╱┃┃━┫╱┃╭╮┃┃┃┃┃╱┃┃┃╰╯┃┃┃┃╰╯┃╱┃╰╮╱
╰━╮╭╯╰━━╯╰━━╯╱╰╯╰╯╰╯╱╰━━╯╱╰╯╰╯╰╯╰╯╱╰╯╰━━╯╰╯╰━━╯╱╰━╯!
╭━╯┃
╰━━╯
*/

/*
*
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⣠⠤⠶⠶⠤⠴⢤⠶⠤⠤⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣴⡞⠉⠁⣴⡆⣸⣿⣿⣿⣿⠛⣷⣌⡻⣦⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⠘⠿⣿⣿⣿⣿⣿⣿⣦⣿⣿⣿⣿⣿⣿⣮⢻⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⣯⣀⣶⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡟⢿⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡿⢈⣽⣿⣿⠟⠛⠛⠉⠛⠉⠁⠀⠀⠀⠘⢻⣿⣧⢸⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢥⣼⣿⡟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⣿⣿⢘⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣿⣿⡇⠀⣀⣀⣀⣠⣄⠀⠀⢠⣤⣄⣀⠀⣿⡿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⢿⢿⣿⡇⠀⠻⣿⣭⣽⢹⣇⠀⠘⣿⣶⣮⠟⢹⣇⢛⡁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⣘⣿⡇⠀⠚⠉⠀⠂⠈⣙⠀⠀⠈⠀⠀⠀⣘⣻⠿⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡺⣧⠈⢷⡄⠀⠀⠀⠀⢠⣶⣤⠀⢠⡄⢀⡄⢸⣿⣴⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠣⣽⣶⣾⣄⡀⠀⠀⠀⢘⣇⣉⣀⡀⠃⠘⣶⢫⣴⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠃⠈⣿⣇⠀⠀⠀⢿⡿⡶⢿⣟⣠⣤⣏⣾⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣤⢹⡽⠆⠀⠀⢀⡽⠷⢶⣶⠶⣯⣯⡍⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣴⡟⢸⣷⠀⠀⠀⠀⢀⣠⢿⣿⡀⣿⡇⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⣾⣿⣿⢸⣿⠀⠀⠀⠀⠈⠁⣸⡏⡷⣏⢻⣦⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⣶⣿⣿⣿⣿⣿⣿⡆⠻⣆⢠⠂⣠⣀⣰⣟⣻⣯⣸⠈⣿⣿⣿⣿⣶⣦⣤⣀⡀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⣀⣤⣶⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠙⢧⡀⠀⠀⠸⣯⣿⣿⣽⠀⢹⣿⣿⣿⣿⣿⣿⣿⣿⣷⣦⣄⡀⠀⠀
⠀⠀⢀⣠⣴⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣇⣄⠈⢻⣄⠀⠀⢩⣟⡈⠁⠀⠀⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣦⠀
⠀⣴⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠈⠀⠀⠈⠓⠶⠿⠀⠀⠀⠀⠀⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡄
⠰⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⠿⣿⣿⣿⣿⣿⣦⡔⠶⠶⢦⣤⣤⠀⠀⣤⣀⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇
⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠁⢰⣼⠿⠿⣿⣿⣿⣿⠛⠛⠒⠶⠶⠶⢤⣠⣬⣽⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷
⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠁⡞⣿⠉⠈⠀⠛⠻⢿⣿⣟⠛⠓⠒⠶⠶⢾⣧⣶⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⢹⣇⢻⣿⣿⣿⣿⣿⣿⣿⣿⠇⢾⠃⠙⠓⠒⠰⣴⣶⣾⣿⣿⠛⠛⠛⠒⠒⢻⠠⣶⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⠈⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣀⣸⠀⠀⠀⣤⠀⠈⢻⣿⣿⣿⡛⠛⠛⠛⠒⠚⠶⢶⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣏⠁⠀⠈⠀⢬⣤⣶⣿⣿⣿⣿⣟⠉⠛⠛⠛⣿⡾⢶⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
*
* */
