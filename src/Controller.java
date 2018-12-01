import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Kontroler w modelu MVC. Ma w sobie obiekt typu view, obiekt model, petle gry,
 * buttony odpowiadajace za logike menu, i stale globalne wielkosci okna, stosunku
 * wysokosci do polowy dlugosci okna i skale okna zalezna od rozdzielczosci monitora.
 */
public class Controller
{
    private View view;
    private Model model;
    private GameLoop game;
    private static JButton play=new JButton("Play"),settings=new JButton("Settings"), quit=new JButton("Quit"), reset=new JButton("OK"), accept=new JButton("OK");
    /**
     * SCALE to skla okna i obiektow, zalezna od rozdzielczosci, RATIO to stosunek
     * wysokosci okna do polowy jego szerokosci, SIZE to polowa szerokosci okna.
     */
    static final double SCALE, RATIO;
    static final int SIZE;
    static
    {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        SCALE=d.getWidth()/2560;
        RATIO=1.2;
        SIZE=(int) (1000*SCALE);
    }

    /**
     * konstruktor kontrolera ustawia logike buttonow, dodaje model i view oraz tworzy petle gry.
     * @param view obiekt View
     * @param model obiekt Model
     */
    private Controller(View view, Model model)
    {
        Menulistener menulistener;
        this.view=view;
        this.model=model;
        game=new GameLoop();
        menulistener=new Menulistener();
        play.addActionListener(menulistener);
        settings.addActionListener(menulistener);
        quit.addActionListener(menulistener);
        reset.addActionListener(menulistener);
        accept.addActionListener(menulistener);
    }

    /**
     * rozpoczyna gre i zamyka menu, szykuje takze button accept na koniec gry
     */
    private void start()
    {
        game.run();
        play.removeAll();
        settings.removeAll();
        quit.removeAll();
        reset.removeAll();
        accept.addActionListener(new Menulistener());
    }

    /**
     * zasadnicza petla programu, jej opuszczenie nastepuje za zamknieciem okna
     * badz System.quit().
     * @param args args
     */
    public static void main(String args[])
    {
        View view=new View(play, settings, quit);
        while(true)
        {
            Model model=new Model();
            Controller controller=new Controller(view, model);
            controller.start();
        }
    }

    /**
     * zasadnicza petla gry, tu odbywa sie logika
     */
    class GameLoop extends Thread
    {
        /**
         * logika gry, najpierw czeka na klikniecie play (odpauzowanie modelu). Nastepnie
         * do view ladowane sa pozycje przeciwnikow i gracza i rozne dane dotyczace gry
         * (poziom, wynik, zycia). Nastepnie kaze view wyswietlic napis "STAGE 1", oraz
         * namalowac wszystko. Potem rozpoczyna sie gra.
         * <p>
         * Naturalnie najpierw sprawdzana jest pauza, a nastepnie to, czy gracz jeszcze
         * zyje. Po sprawdzeniu pauzy petla wraca na poczatek, model czeka na odpauzowanie.
         * Jezeli gracz przegral, podjete sa odpowiednie akcje zeby to zasygnalizowac, oraz
         * wyswietlane jest menu informujace o porazce. Petla posiada takze zegar, dyktujacy
         * "dlugosc" kroku jaka maja wykonac wszystkie obiekty modelu. Zegar dyktuje takze
         * czas spania, obie zmienne dobierane sa tak aby przez 1 sekunde model sumarycznie
         * wykonal 100 krokow.
         * <p>
         * Po wykonaniu krokow, sprawdzane jest czy gracz zostal trafiony, oraz czy gracz cos
         * trafil. Jezeli gracz zostal trafiony, view dostaje polecenie poczynienia odpowiednich
         * krokow by to pokazac, oraz model zostaje poinformowany o tym zdarzeniu. Nastepnie model
         * jest pauzowany. Potem do view ladowane sa pozycje przeciwnikow i pociskow,  view zostaje
         * przemalowany. W koncu zostaje sprawdzone czy gracz wygral, i ewentualna informacja o tym
         * wysylana do view, oraz zostaje wygenerowany nowy poziom.
         */
        public void run()
        {
            long newtime, oldtime;
            double counter=0, sleeptime=0;
            try
            {
                model.pause();
                while(model.getpause()) sleep(10);
                view.load(model.getPositions());
                view.loadmisc(model.getlives(), model.getscore(), model.getstage());
                view.dowin();
                view.repaint();
                model.pause();
                oldtime=System.nanoTime();
                while(true)
                {
                    if(model.getpause())
                    {
                        sleep(10);
                        oldtime=System.nanoTime();
                        continue;
                    }
                    if(model.getlives()<=0)
                    {
                        view.loadmisc(model.getlives(), model.getscore(),  model.getstage());
                        view.dolose(reset);
                        break;
                    }
                    newtime=System.nanoTime()-oldtime;
                    model.movement((int) (newtime/10000000)+(int)counter);
                    counter+=(double)newtime/10000000-(int)(newtime/10000000)-(int)counter;
                    if(model.hit())
                    {
                        view.load(model.getPositions(), model.getShipRockets(), model.getRockets());
                        view.loadmisc(model.getlives(), model.getscore(),  model.getstage());
                        view.death();
                        view.repaint();
                        model.shotdown();
                        model.pause();
                        continue;
                    }
                    view.load(model.getPositions(), model.getShipRockets(), model.getRockets());
                    view.loadmisc(model.getlives(), model.getscore(),  model.getstage());
                    view.repaint();
                    if(model.checkWin())
                    {
                        if(model.getstage()%6!=0) model.generator();
                        else model.boss();
                        view.load(model.getPositions(), null, null, null);
                        view.loadmisc(model.getlives(), model.getscore(), model.getstage());
                        view.dowin();
                        view.repaint();
                        model.pause();
                    }
                    oldtime=System.nanoTime();
                    sleeptime-=(double)newtime/1000000;
                    sleeptime+=10;
                    if(sleeptime>0) sleep((int) sleeptime);
                }
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Listener klawiatury. Strzalka w prawo oznacza ruch w prawo, strzalka
     * w lewo ruch w lewo, spacja oznacza strzal, strzalka w dol oznacza pauze.
     * UWAGA: strzalka w gore sprawia ze nastepny poziom zostaje wygenerowany.
     * Funkcja nei sluzy do uzytku, lecz do przeciazania generatora, i w
     * ewentualnym wydaniu gry nalezy sie jej pozbyc.
     */
    class Keyboard implements KeyListener
    {
        public void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode()==KeyEvent.VK_LEFT) model.logic(-1);
            if (e.getKeyCode()==KeyEvent.VK_RIGHT) model.logic(1);
            if (e.getKeyCode()==KeyEvent.VK_SPACE) model.shoot(true);
            if (e.getKeyCode()==KeyEvent.VK_DOWN) model.pause();
            if (e.getKeyCode()==KeyEvent.VK_UP) model.skip();
        }
        public void keyReleased(KeyEvent e)
        {
            if (e.getKeyCode()==KeyEvent.VK_LEFT) model.logic(-2);
            if (e.getKeyCode()==KeyEvent.VK_RIGHT) model.logic(2);
            if (e.getKeyCode()==KeyEvent.VK_SPACE) model.shoot(false);
        }
        public void keyTyped(KeyEvent e) {}
    }

    /**
     * Listener do menu dla przeroznych buttonow. Button play oznacza poczatek gry,
     * button quit oznacza zamkniecie menu, button reset oznacza ponowne wywolanie
     * menu glownego, button settings otwiera pole tekstowe sluzace do wpisania
     * seed-u, button accept oznacza zaakcpetowanie zmian dokonanych w oknie settings.
     */
    class Menulistener implements ActionListener
    {
        private JTextField seed=new JTextField(11);
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource()==play)
            {
                view.game();
                view.addKeyListener(new Keyboard());
                model.pause();
            }
            if(e.getSource()==quit)
            {
                System.exit(0);
            }
            if(e.getSource()==reset)
            {
                view.menu(275, 75, null, play, settings, quit);
            }
            if(e.getSource()==settings)
            {
                view.menu(275, 75, "seed: ");
                view.add(seed);
                view.add(accept);
            }
            if(e.getSource()==accept)
            {
                double[] arr=new double[11];
                String string=seed.getText();
                if(string.length()<1) arr[0]=128;
                else arr[0]=(string.charAt(0)%128)+128;
                if(string.length()<2) arr[1]=128;
                else if(string.charAt(1)=='0') arr[1]=0;
                else arr[1]=(string.charAt(1)%128)+128;
                if(string.length()<3) arr[2]=64;
                else if(string.charAt(2)=='0') arr[2]=0;
                else arr[2]=(string.charAt(2)%192)+64;
                if(string.length()<4) arr[3]=21;
                else if(string.charAt(3)=='0') arr[3]=0;
                else arr[3]=(string.charAt(3)%21)+21;
                if(string.length()<5) arr[4]=16;
                else if(string.charAt(4)=='0') arr[4]=0;
                else arr[4]=(string.charAt(4)%16)+16;
                if(string.length()<6) arr[5]=8;
                else if(string.charAt(5)=='0') arr[5]=0;
                else arr[5]=(string.charAt(5)%8)+8;
                if(string.length()<7) arr[6]=16;
                else if(string.charAt(6)=='0') arr[6]=0;
                else arr[6]=(string.charAt(6)%16)+16;
                if(string.length()<8) arr[7]=11;
                else if(string.charAt(7)=='0') arr[7]=0;
                else arr[7]=(string.charAt(7)%21)+11;
                if(string.length()<9) arr[8]=16;
                else if(string.charAt(8)=='0') arr[8]=0;
                else arr[8]=(string.charAt(8)%16)+16;
                if(string.length()<10) arr[9]=arr[0]+1;
                else arr[9]=(string.charAt(9)%128)+arr[0]+1;
                if(string.length()<11) arr[10]=947*945;
                else arr[10]=string.charAt(10)*947+947*945;
                model.loadseed(arr);
                model.generator();
                view.menu(275, 75, null, play, settings, quit);
            }
        }
    }
}
