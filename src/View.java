import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;

/**
 * Modul view modelu MVC. Zawiera w sobie wszystkie obrazy, a takze rozne dane
 * ktore nalezy wyswietlac oraz glowna tablice intow z pozycjami i numerami jak,
 * gdzie i co nalezy wyswietlic. Jest Jpanele i zawiera w sobie Jframes menu oraz
 * gry i odpowiednie labele. Button back jest tu z powodow czysto estetycznych.
 */
public class View extends JPanel
{
    private Image sprite0, sprite1, sprite2, sprite3, sprite4, sprite5, sprite6, sprite7, sprite8, sprite9, splinteredsprite9, Gargantua, backdrop;
    private int[][] positions;
    private int level, lives, score;
    private int height=(int)(Controller.SIZE*Controller.RATIO);
    private JFrame gamemenu, game;
    private JLabel liveslab, levellab, scorelab, popup, death, temp;
    private JButton back;

    /**
     * konstruktor ustala stan poczatkowy, laduje obrazy z resources, tworzy
     * odpowiednie Jlabele i otwiera menu z gra.
     * @param buttons Przyciski z jakimi otworzy sie menu
     */
    View(JButton...buttons)
    {
        Font font=new Font("TimesRoman", Font.BOLD, (int)(13*Controller.SCALE));
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        liveslab=new JLabel("lives: ");
        levellab=new JLabel("stage: ");
        scorelab=new JLabel("score: ");
        popup=new JLabel("STAGE ");
        death=new JLabel("You Died!");
        temp=new JLabel();
        liveslab.setFont(font);
        levellab.setFont(font);
        scorelab.setFont(font);
        temp.setFont(font);
        font=new Font("TimesRoman", Font.BOLD, (int)(50*Controller.SCALE));
        popup.setFont(font);
        death.setFont(font);
        popup.setForeground(Color.WHITE);
        death.setForeground(Color.WHITE);
        level=0;
        lives=0;
        score=0;
        menu(275, 75, null, buttons);
        try
        {
            sprite0=ImageIO.read(new File("resources/Sprite0.png")).getScaledInstance((int) (Controller.SCALE*20), -1, Image.SCALE_SMOOTH);
            sprite1=ImageIO.read(new File("resources/Sprite1.png")).getScaledInstance((int) (Controller.SCALE*50), -1, Image.SCALE_SMOOTH);
            sprite2=ImageIO.read(new File("resources/Sprite2.png")).getScaledInstance((int) (Controller.SCALE*50), -1, Image.SCALE_SMOOTH);
            sprite3=ImageIO.read(new File("resources/Sprite3.png")).getScaledInstance((int) (Controller.SCALE*50), -1, Image.SCALE_SMOOTH);
            sprite4=ImageIO.read(new File("resources/Sprite4.png")).getScaledInstance((int) (Controller.SCALE*40), -1, Image.SCALE_SMOOTH);
            sprite5=ImageIO.read(new File("resources/Sprite5.png")).getScaledInstance((int) (Controller.SCALE*40), -1, Image.SCALE_SMOOTH);
            sprite6=ImageIO.read(new File("resources/Sprite6.png")).getScaledInstance((int) (Controller.SCALE*60), -1, Image.SCALE_SMOOTH);
            sprite7=ImageIO.read(new File("resources/Sprite7.png")).getScaledInstance((int) (Controller.SCALE*60), -1, Image.SCALE_SMOOTH);
            sprite8=ImageIO.read(new File("resources/Sprite8.png")).getScaledInstance((int) (Controller.SCALE*30), -1, Image.SCALE_SMOOTH);
            sprite9=ImageIO.read(new File("resources/Sprite9.png")).getScaledInstance((int) (Controller.SCALE*50), -1, Image.SCALE_SMOOTH);
            Gargantua=ImageIO.read(new File("resources/Gargantua.png")).getScaledInstance((int) (Controller.SCALE*200), -1, Image.SCALE_SMOOTH);
            splinteredsprite9=ImageIO.read(new File("resources/SplinteredSprite9.png")).getScaledInstance((int) (Controller.SCALE*50), -1, Image.SCALE_SMOOTH);
            backdrop=ImageIO.read(new File("resources/Backdrop.png")).getScaledInstance((int) (Controller.SCALE*2000), -1, Image.SCALE_SMOOTH);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * malowanie tla w grze.
     * @param g obiekt Graphics
     */
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(lives!=0)
        {
            g.drawImage(backdrop, 0, 0-height, this);
            g.setColor(Color.WHITE);
            g.fillRect(0, (int)(Controller.SIZE*Controller.RATIO-55*Controller.SCALE), (int)(250*Controller.SCALE), (int)(50*Controller.SCALE));
        }
        else
        {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 500, 500);
        }
    }

    /**
     * otworzenie menu z odpowiednim rozmiarem, labelem oraz buttonami.
     * @param x rozmiar x
     * @param y rozmiar y
     * @param string jaki ma byc label
     * @param buttons buttony w menu
     */
    void menu(int x, int y, String string, JButton...buttons)
    {
        quit();
        this.removeAll();
        temp.setText(string);
        this.add(temp);
        gamemenu=new JFrame("Galaxy");
        for(JButton button: buttons)
        {
            button.setLocation(0, 0);
            this.add(button);
        }
        gamemenu.add(this);
        gamemenu.setSize(x,y);
        gamemenu.setLocationRelativeTo(null);
        gamemenu.setVisible(true);
        gamemenu.setResizable(false);
        gamemenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
    }

    /**
     * otwarcie zasadniczego Jframe z gra
     */
    void game()
    {
        quit();
        this.removeAll();
        this.add(liveslab);
        this.add(levellab);
        this.add(scorelab);
        this.add(popup);
        this.add(death);
        game = new JFrame("Galaxy");
        game.add(this);
        game.setSize(2*Controller.SIZE, (int)(Controller.SIZE*Controller.RATIO));
        game.setLocationRelativeTo(null);
        game.setVisible(true);
        game.setResizable(false);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
    }

    /**
     * funkcje do zrobienia po wygraniu poziomu, i.e. wyswietlenie odpowiedniego labela
     */
    void dowin()
    {
        popup.setText("STAGE "+level);
        popup.setVisible(true);
    }

    /**
     * funkcje do zrobienia jezeli gracz przegra
     * @param button button do menu game over
     */
    void dolose(JButton button)
    {
        quit();
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        menu(500, 250, "Game Over", button);
        this.add(levellab);
        this.add(scorelab);
        back=button;
        positions=null;
    }

    /**
     * zamkniecie gamemenu i game, jezeli sa
     */
    private void quit() {if(game!=null) game.dispose(); if(gamemenu!=null) gamemenu.dispose();}

    /**
     * wyswietlenie informacji o smierci
     */
    void death(){death.setVisible(true);}

    /**
     * zaladowanie danych podanych przez kontroler o pozycjach i numerach obrazow
     * @param arr tablica danych
     */
    void load(int[][]...arr)
    {
        height-=3;
        if (height<=0) height=(int) (Controller.SIZE*Controller.RATIO);
        popup.setVisible(false);
        death.setVisible(false);
        if(arr[0]==null) return;
        int sum=0;
        for(int[][] i: arr) if(i!=null) sum+=i.length;
        positions=new int[sum][4];
        sum=0;
        for(int[][] i :arr)
        {
            if(i!=null)
            {
                System.arraycopy(i, 0, positions, sum, i.length);
                sum+=i.length;
            }
        }
    }

    /**
     * pozsotale dane do zaladowania
     * @param liv pozostale zycia
     * @param pts wynik
     * @param lvl poziom
     */
    void loadmisc(int liv, int pts, int lvl) {lives=liv; score=pts; level=lvl;}

    /**
     * funkcja paint malujaca wszystkie elementy i ustawiajaca labely na miejscu
     * @param g obiekt Graphics
     */
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        popup.setLocation(Controller.SIZE-100, Controller.SIZE/2);
        death.setLocation(Controller.SIZE-110, Controller.SIZE*4/5);
        if(lives==0)
        {
            levellab.setLocation(220, 100);
            scorelab.setLocation(220, 130);
            if(temp!=null&&temp.getText()=="Game Over") temp.setLocation(210, 0);
            if(back!=null) back.setLocation(220, 50);
        }
        else
        {
            levellab.setLocation(0, (int)(Controller.SIZE*Controller.RATIO-55));
            liveslab.setLocation((int)(75*Controller.SCALE), (int)(Controller.SIZE*Controller.RATIO-55*Controller.SCALE));
            scorelab.setLocation((int)(150*Controller.SCALE), (int)(Controller.SIZE*Controller.RATIO-55*Controller.SCALE));
            levellab.setText("stage: "+level);
            liveslab.setText("lives: "+lives);
            scorelab.setText("score: "+score);
        }
        if(positions!=null)
        {
            for(int[] i: positions)
            {
                switch (i[3])
                {
                    case 1:
                        g.drawImage(sprite1, i[0]-i[2], i[1]-i[2],this);
                        break;
                    case 2:
                        g.drawImage(sprite2, i[0]-i[2], i[1]-i[2],this);
                        break;
                    case 3:
                        g.drawImage(sprite3, i[0]-i[2], i[1]-i[2],this);
                        break;
                    case 4:
                        g.drawImage(sprite4, i[0]-i[2], i[1]-i[2],this);
                        break;
                    case 5:
                        g.drawImage(sprite5, i[0]-i[2], i[1]-i[2],this);
                        break;
                    case 6:
                        g.drawImage(sprite6, i[0]-i[2], i[1]-i[2],this);
                        break;
                    case -6:
                        g.drawImage(sprite6, i[0]-i[2], i[1]-i[2],this);
                        g.setColor(Color.RED);
                        g.fillOval(i[0]-i[2]/2, i[1], i[2], i[2]);
                        break;
                    case -66:
                        g.drawImage(sprite6, i[0]-i[2], i[1]-i[2],this);
                        g.setColor(Color.RED);
                        g.fillOval(i[0]-i[2]/2, i[1], i[2], i[2]);
                        g.fillRect(i[0]-i[2]/2,i[1]+i[2]/2, i[2], (int)(Controller.SIZE*Controller.RATIO)-i[1]-i[2]);
                        break;
                    case 7:
                        g.drawImage(sprite7, i[0]-i[2], i[1]-i[2],this);
                        break;
                    case 8:
                        g.drawImage(sprite8, i[0]-i[2], i[1]-i[2],this);
                        break;
                    case 9:
                        g.drawImage(sprite9, i[0]-i[2], i[1]-i[2], this);
                        break;
                    case -9:
                        g.drawImage(splinteredsprite9, i[0]-i[2], i[1]-i[2], this);
                        break;
                    case 10:
                        g.drawImage(Gargantua, i[0]-i[2], i[1]-i[2], this);
                        break;
                    case -10:
                        g.drawImage(Gargantua, i[0]-i[2], i[1]-i[2], this);
                        g.setColor(Color.RED);
                        g.fillOval(i[0]-i[2]*3/7, i[1]+i[2]*6/7, i[2]*2/7, i[2]*2/7);
                        g.fillOval(i[0]+i[2]/7, i[1]+i[2]*6/7, i[2]*2/7, i[2]*2/7);
                        g.fillOval(i[0]-i[2], i[1]+i[2]*5/7, i[2]*2/7, i[2]*2/7);
                        g.fillOval(i[0]+i[2]*5/7, i[1]+i[2]*5/7, i[2]*2/7, i[2]*2/7);
                        break;
                    case -1010:
                        g.drawImage(Gargantua, i[0]-i[2], i[1]-i[2], this);
                        g.setColor(Color.RED);
                        g.fillOval(i[0]-i[2]*3/7, i[1]+i[2]*6/7, i[2]*2/7, i[2]*2/7);
                        g.fillOval(i[0]+i[2]/7, i[1]+i[2]*6/7, i[2]*2/7, i[2]*2/7);
                        g.fillOval(i[0]-i[2], i[1]+i[2]/7*5, i[2]*2/7, i[2]*2/7);
                        g.fillOval(i[0]+i[2]*5/7, i[1]+i[2]/7*5, i[2]*2/7, i[2]*2/7);
                        g.fillRect(i[0]-i[2]*3/7, i[1]+i[2], i[2]*2/7, (int)(Controller.SIZE*Controller.RATIO)-i[1]);
                        g.fillRect(i[0]+i[2]/7, i[1]+i[2], i[2]*2/7, (int)(Controller.SIZE*Controller.RATIO)-i[1]);
                        g.fillRect(i[0]-i[2], i[1]+i[2]/7*6, i[2]*2/7, (int)(Controller.SIZE*Controller.RATIO)-i[1]);
                        g.fillRect(i[0]+i[2]*5/7, i[1]+i[2]/7*6, i[2]*2/7, (int)(Controller.SIZE*Controller.RATIO)-i[1]);
                        break;
                    case 100:
                        g.drawImage(sprite0,i[0]-i[2],i[1]-i[2], this);
                        break;
                    case -1:
                        g2d.setColor(Color.MAGENTA);
                        g.fillOval(i[0], i[1], 5, 5);
                        break;
                    case -2:
                        g2d.setColor(Color.YELLOW);
                        g.fillOval(i[0], i[1], 5, 5);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}