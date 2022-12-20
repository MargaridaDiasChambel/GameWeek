import org.academiadecodigo.simplegraphics.graphics.Color;
import org.academiadecodigo.simplegraphics.graphics.Text;
import org.academiadecodigo.simplegraphics.keyboard.Keyboard;
import org.academiadecodigo.simplegraphics.keyboard.KeyboardEvent;
import org.academiadecodigo.simplegraphics.keyboard.KeyboardEventType;
import org.academiadecodigo.simplegraphics.keyboard.KeyboardHandler;
import org.academiadecodigo.simplegraphics.pictures.Picture;

import java.util.ArrayList;

import static org.academiadecodigo.simplegraphics.keyboard.KeyboardEvent.KEY_S;
import static org.academiadecodigo.simplegraphics.keyboard.KeyboardEvent.KEY_SPACE;

public class Game implements KeyboardHandler {

    private boolean start;

    private boolean skipIntro;
    private boolean skipIntro2;

    private boolean skipIntro3;

    private boolean skipIntro4;

    //number of poops you don't caught that make you lose the game
    private static final int POOPSTOLOSE = 15;

    //number of poops you caught that make you win the game
    private static final int POOPSTOWIN = 30;

    private static class ShinChan {

        private final Picture shin;

        //totalPoops = increment everytime Shin Chan poops
        private int totalPoops;

        // in this arrayList it will be saved all the Poops Shin Chan makes (including Poops, Bombs and ToiletPaper)
        private final ArrayList<Poop> poops;


        //when we create ShinChan we will immediately draw him in the screen
        public ShinChan(int x, int y, String source) {
            this.poops = new ArrayList<>();
            this.shin = new Picture(x, y, source);
            shin.draw();

        }


    }

    public void init() throws InterruptedException {
        initKeyboard();

        Picture startImage = new Picture(10, 10, "Images/Start_game.png");

        Music music = new Music();

        if(!start) {
            // Play intro music
            music.startMusic("Music/shinChan_song.wav");
        }

        while(!start){
            startImage.draw();
        }
        while(!skipIntro) {
            intro1();
        }
        while (!skipIntro2) {
            intro2();
        }
        while (!skipIntro3) {
            intro3();
        }
        while (!skipIntro4) {
            intro4();
        }

        //create background and draw it(make it appear on the screen)
        Picture background = new Picture(10, 10, "Images/game.jpg");
        background.draw();


        //create shin chan, his sister and give them movement
        ShinChan shinChan = new ShinChan(10, 10, "Images/shin.png");

        Player shinSis = new Player(650, 570, "Images/shinSis.png");


         play(shinChan,shinSis);

    }

    //method to keep moving Shin Chan endlessly back and forward
    //make him poop randomly a Poop, Bomb or toiletPaper
    //at the end determines if the Player wins or lose

    public void play( ShinChan shinChan, Player player ) throws InterruptedException {

        //determines the number of pixels he will move to the right
        int shinPositiveVelocity = (int) (Math.random() * 3) + 1;

        //determines the number of pixels he will move to the left
        int shinNegativeVelocity = (int) (Math.random() * -3) -1;

        //iterator for the number of pixels moved by ShinChan
        int pixels = 0;

        //direction: to the right --> true; to the left --> false;
        boolean direction = true;

        //the game will roll until one of the conditions below doesn't verify
        while ( player.getPoints() < POOPSTOWIN && Poop.getLostPoops() < POOPSTOLOSE && player.getLives() > 0){
            //the block of code below will print the total points, the number of lost poops in real time
            String livesStr = Integer.toString(player.getLives());
            Text livesImage = new Text(1382, 268, livesStr);
            livesImage.grow(10,10);
            livesImage.setColor(Color.GREEN);
            livesImage.draw();

            String wonPointsStr = Integer.toString(player.getPoints());
            Text wonPointsI = new Text(1385, 395, wonPointsStr);
            wonPointsI.grow(10,10);
            wonPointsI.setColor(Color.GREEN);
            wonPointsI.draw();

            String lostPoopsStr = Integer.toString(Poop.getLostPoops());
            Text lostPoops = new Text(1385, 520, lostPoopsStr);
            lostPoops.grow(10,10);
            lostPoops.setColor(Color.GREEN);
            lostPoops.draw();


            if(pixels >= 1150) direction = false; // left
            if(pixels <= 0) direction = true; // false

            Thread.sleep(5);
            if(direction) {
                shinChan.shin.translate(shinPositiveVelocity, 0);
                pixels+=shinPositiveVelocity;
            }
            else{
                shinChan.shin.translate(shinNegativeVelocity, 0);
                pixels+=shinNegativeVelocity;
            }

            //random number to help define the position to poop
            int randomPoop = (int) (Math.random() * 100) + 100;

            if (shinChan.shin.getX() % randomPoop == 0) {
                if(randomPoop > 100 && randomPoop < 110 ){
                    Poop poopBomb = new Poop(shinChan.shin.getX() + 55, 115, "Images/bomb.png",'B');
                    //we add the new poop to the array of poops and increment the number of totalPoops
                    shinChan.poops.add(poopBomb);
                } else if (randomPoop > 150 && randomPoop < 160 ){
                    Poop toiletPaper = new Poop(shinChan.shin.getX() + 55, 115, "Images/tp_50px.png",'T');
                    //we add the new poop to the array of poops and increment the number of totalPoops
                    shinChan.poops.add(toiletPaper);
                }
                else {
                    shinChan.totalPoops++;
                    //we set the lastTime Shin pooped to the actual time
                    Poop poop1 = new Poop(shinChan.shin.getX() + 55, 115, "Images/angry_poop_50px.png", 'P');
                    //we add the new poop to the array of poops and increment the number of totalPoops
                    shinChan.poops.add(poop1);
                }

            }

            //we invoke the movePoops method that will make all the poops in the array move
            Poop.movePoops(player,shinChan.poops);

            //delete the points, lost poops, total poops and lives at the screen, this way we can actualize them
            livesImage.delete();
            wonPointsI.delete();
            lostPoops.delete();

            //if you lost 20 or more poops you will lose
            if (Poop.getLostPoops() >= POOPSTOLOSE || player.getLives() <= 0){
                Picture gameOver = new Picture(10, 10, "Images/game_over.png");
                gameOver.draw();
                livesStr = Integer.toString(player.getLives());
                livesImage = new Text(1382, 268, livesStr);
                livesImage.grow(10,10);
                livesImage.setColor(Color.GREEN);
                livesImage.draw();

                wonPointsStr = Integer.toString(player.getPoints());
                wonPointsI = new Text(1385, 395, wonPointsStr);
                wonPointsI.grow(10,10);
                wonPointsI.setColor(Color.GREEN);
                wonPointsI.draw();

                lostPoopsStr = Integer.toString(Poop.getLostPoops());
                lostPoops = new Text(1385, 520, lostPoopsStr);
                lostPoops.grow(10,10);
                lostPoops.setColor(Color.GREEN);
                lostPoops.draw();
            } else if (player.getPoints()>= POOPSTOWIN) {
                //if you caught 10 or more poops you win!!
                Picture youWin = new Picture(10, 10, "Images/win_1.png");
                youWin.draw();
                livesStr = Integer.toString(player.getLives());
                livesImage = new Text(1382, 268, livesStr);
                livesImage.grow(10,10);
                livesImage.setColor(Color.GREEN);
                livesImage.draw();

                wonPointsStr = Integer.toString(player.getPoints());
                wonPointsI = new Text(1385, 395, wonPointsStr);
                wonPointsI.grow(10,10);
                wonPointsI.setColor(Color.GREEN);
                wonPointsI.draw();

                lostPoopsStr = Integer.toString(Poop.getLostPoops());
                lostPoops = new Text(1385, 520, lostPoopsStr);
                lostPoops.grow(10,10);
                lostPoops.setColor(Color.GREEN);
                lostPoops.draw();
            }

        }



    }


    public void intro1() {
        Picture intro1 = new Picture(10, 10, "Images/Intro/fala_1.png");
        intro1.draw();
    }

    public void intro2() {

        Picture intro2 = new Picture(10, 10, "Images/Intro/fala_2.png");
        intro2.draw();
    }

    public void intro3() {
        Picture intro3 = new Picture(10, 10, "Images/Intro/fala_3.png");
        intro3.draw();
    }
    public void intro4() {
        Picture intro4 = new Picture(10, 10, "Images/Intro/fala_4.png");
        intro4.draw();
    }


    @Override
    public void keyPressed(KeyboardEvent keyboardEvent) {
        int key = keyboardEvent.getKey();

        if (key == KEY_SPACE) {
            start= true;
        } else if (key == KEY_S){
            if(!skipIntro){
            skipIntro = true;
            return;
            }
            else if(!skipIntro2 && skipIntro){
                skipIntro2 = true;
                return;
            } else if (!skipIntro3 && skipIntro2) {
                skipIntro3 = true;
                return;
            } else if (!skipIntro4 && skipIntro3){
                skipIntro4 = true;
            }
        }

    }

    @Override
    public void keyReleased(KeyboardEvent keyboardEvent) {

    }

    public void initKeyboard(){
        Keyboard keyboard = new Keyboard(this);

        KeyboardEvent keyPressedSpace = new KeyboardEvent();
        keyPressedSpace.setKey(KEY_SPACE);
        keyPressedSpace.setKeyboardEventType(KeyboardEventType.KEY_PRESSED);
        keyboard.addEventListener(keyPressedSpace);

        KeyboardEvent keySkipIntro = new KeyboardEvent();
        keySkipIntro.setKey(KEY_S);
        keySkipIntro.setKeyboardEventType(KeyboardEventType.KEY_PRESSED);
        keyboard.addEventListener(keySkipIntro);


    }
}


