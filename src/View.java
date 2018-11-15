import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;
class View extends JPanel
{
    private Image sprite0;
    private Image sprite1;
    private Image sprite2;
    private Image sprite3;
    private Image sprite4;
    private Image sprite5;
    private Image sprite6;
    private Image sprite7;
    private Image sprite8;
    private Image sprite9;
    private Image splinteredsprite9;
    private Image Gargantua;
    private Image backdrop;
    private int[][] positions;
    private int stage, left, points;
    private int height=Controller.SIZE*6/5;
    private JFrame gamemenu, game;
    private JLabel lives, level, score, popup, death;
    View(JButton...buttons)
    {
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        lives=new JLabel("lives: ");
        level=new JLabel("stage: ");
        score=new JLabel("score: ");
        popup=new JLabel("STAGE ");
        death=new JLabel("You Died!");
        popup.setFont(new Font("TimesRoman", Font.BOLD, 50));
        death.setFont(new Font("TimesRoman", Font.BOLD, 50));
        popup.setForeground(Color.WHITE);
        death.setForeground(Color.WHITE);
        stage=0;
        left=0;
        points=0;
        menu(275, 75, null, buttons);
        try
        {
            sprite0 = ImageIO.read(new File("resources/Sprite0.png"));
            sprite1 = ImageIO.read(new File("resources/Sprite1.png"));
            sprite2 = ImageIO.read(new File("resources/Sprite2.png"));
            sprite3 = ImageIO.read(new File("resources/Sprite3.png"));
            sprite4 = ImageIO.read(new File("resources/Sprite4.png"));
            sprite5 = ImageIO.read(new File("resources/Sprite5.png"));
            sprite6 = ImageIO.read(new File("resources/Sprite6.png"));
            sprite7 = ImageIO.read(new File("resources/Sprite7.png"));
            sprite8 = ImageIO.read(new File("resources/Sprite8.png"));
            sprite9 = ImageIO.read(new File("resources/Sprite9.png"));
            Gargantua=ImageIO.read(new File("resources/Gargantua.png"));
            splinteredsprite9 = ImageIO.read(new File("resources/SplinteredSprite9.png"));
            backdrop = ImageIO.read(new File("resources/Backdrop.png"));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(left!=0)
        {
            g.drawImage(backdrop, 0, 0-height, this);
            g.setColor(Color.WHITE);
            g.fillRect(0, Controller.SIZE*6/5-55, 220, 50);
        }
        else
        {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 500, 500);
        }
    }
    public void menu(int x, int y, String string, JButton...buttons)
    {
        quit();
        this.removeAll();
        if(string!=null) this.add(new JLabel(string));
        gamemenu = new JFrame("Galaxy");
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
    public void game()
    {
        quit();
        this.removeAll();
        this.add(lives);
        this.add(level);
        this.add(score);
        this.add(popup);
        this.add(death);
        stage=0;
        game = new JFrame("Galaxy");
        game.add(this);
        game.setSize(2 * Controller.SIZE, Controller.SIZE*6/5);
        game.setLocationRelativeTo(null);
        game.setVisible(true);
        game.setResizable(false);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
    }
    public void dowin()
    {
        popup.setText("STAGE "+stage);
        popup.setVisible(true);
    }
    public void dolose(JButton button)
    {
        quit();
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        stage=0;
        menu(500, 500, "Game Over", button);
        positions=null;
    }
    private void quit() {if(game!=null) game.dispose(); if(gamemenu!=null) gamemenu.dispose();}
    public void death(){death.setVisible(true);}
    public void load(int[][]...arr)
    {
        height-=3;
        if (height<=0) height=Controller.SIZE*6/5;
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
    public void loadmisc(int liv, int scor, int lvl) {left=liv; points=scor; stage=lvl;}
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        popup.setLocation(Controller.SIZE-100, Controller.SIZE/2);
        death.setLocation(Controller.SIZE-110, Controller.SIZE*4/5);
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
                        g.fillRect(i[0]-i[2]/2,i[1]+i[2]/2, i[2], Controller.SIZE*6/5-i[1]-i[2]);
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
                        g.fillRect(i[0]-i[2]*3/7, i[1]+i[2], i[2]*2/7, Controller.SIZE*6/5-i[1]);
                        g.fillRect(i[0]+i[2]/7, i[1]+i[2], i[2]*2/7, Controller.SIZE*6/5-i[1]);
                        g.fillRect(i[0]-i[2], i[1]+i[2]/7*6, i[2]*2/7, Controller.SIZE*6/5-i[1]);
                        g.fillRect(i[0]+i[2]*5/7, i[1]+i[2]/7*6, i[2]*2/7, Controller.SIZE*6/5-i[1]);
                        break;
                    case 100:
                        g.drawImage(sprite0,i[0]-i[2],i[1]-i[2], this);
                        level.setLocation(0, i[1]+i[2]*2);
                        lives.setLocation(75, i[1]+i[2]*2);
                        score.setLocation(150, i[1]+i[2]*2);
                        level.setText("stage: "+stage);
                        lives.setText("lives: "+left);
                        score.setText("score: "+points);
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