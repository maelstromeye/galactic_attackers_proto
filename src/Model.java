import java.awt.event.KeyEvent;
import java.lang.StrictMath;
import java.util.List;
import java.util.Vector;

class Model
{
    private int difficulty;
    private int lives;
    private Ship ship;
    List list;
    private SmallFry[] enemies;
    private Runner[] hostiles;
    Model()
    {
        list=new Vector();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////list.add(new Runner());
        lives=3;
        difficulty=1;
        ship=new Ship();
        enemies=new SmallFry[10];
        hostiles=new Runner[10];
        this.generator();
    }
    public void generator()
    {
        for (int i = 0; i < 10; i++)
        {
            enemies[i] = new SmallFry();
            enemies[i].setx(25+i*50);
            enemies[i].setlr(Controller.SIZE-3*enemies[i].getr()+i*50);
            enemies[i].setll(i*50);
            hostiles[i]=new Runner();
            hostiles[i].setx(25+i*50);
            hostiles[i].setlr(Controller.SIZE-3*hostiles[i].getr()+i*50);
            hostiles[i].setll(i*50);
        }
    }
    public int[][] getPositions()
    {
        int[][] data=new int[enemies.length+hostiles.length+1][4];
        for(int i=0; i<enemies.length; i++)
        {
            data[i][0]=enemies[i].getx();
            data[i][1]=enemies[i].gety();
            data[i][2]=enemies[i].getr();
            data[i][3]=enemies[i].gets();
        }
        for(int i=0; i<hostiles.length; i++)
        {
            data[i+enemies.length][0]=hostiles[i].getx();
            data[i+enemies.length][1]=hostiles[i].gety();
            data[i+enemies.length][2]=hostiles[i].getr();
            data[i+enemies.length][3]=hostiles[i].gets();
        }
        data[enemies.length+hostiles.length][0]=ship.getx();
        data[enemies.length+hostiles.length][1]=(Controller.SIZE-3*ship.getr());
        data[enemies.length+hostiles.length][2]=ship.getr();
        data[enemies.length+hostiles.length][3]=100;
        return data;
    }
    public int[][] getMissiles()
    {
        if(ship.gets()==0) return null;
        int[][] data=new int[ship.gets()][4];
        for(int i=0;i<ship.gets();i++)
        {
            data[i]=ship.extract(i);
        }
        return data;
    }
    public void movement(int i)
    {
        for(SmallFry fry:enemies)
        {
            fry.movement(i);
        }
        for(Runner run:hostiles)
        {
            run.movement(i);
        }
    }
    public int[][] hit()
    {
        int j = 0;
        int k=0;
        int[][] data = getPositions();
        int[][] rockets = getMissiles();
        double dist = 0;
        if (rockets == null) return data;
        for (int i = 0; i < data.length-1-k; i++)
        {
            if(rockets[0]==null)   break;
            dist = ((data[i][0] - rockets[0][0])*(data[i][0] - rockets[0][0]) + (data[i][1] - rockets[0][1])*(data[i][1] - rockets[0][1]));
            dist = Math.sqrt(dist);
            if (dist <= data[i][2])
            {
                kill(i);
                ship.shotdown(0);
                for (j = i; j< data.length-k-1; j++)
                {
                    data[j] = data[j + 1];
                }
                data[j] = null;
                for (j = 0; j< rockets.length-k-1; j++)
                {
                    rockets[j] = rockets[j + 1];
                }
                rockets[j] = null;
                k++;
                continue;
            }
            if (rockets.length-k>= 2) {
                dist = (double) ((data[i][0] - rockets[1][0])*(data[i][0] - rockets[1][0]) + (data[i][1] - rockets[1][1])*(data[i][1] - rockets[1][1]));
                dist = Math.sqrt(dist);
                if (dist <= data[i][2])
                {
                    kill(i);
                    ship.shotdown(1);
                    for (j = i; j< data.length-k-1; j++)
                    {
                        data[j] = data[j + 1];
                    }
                    data[j] = null;
                    for (j = 1; j< rockets.length-k-1; j++)
                    {
                        rockets[j] = rockets[j + 1];
                    }
                    rockets[j] = null;
                    k++;
                    continue;
                }
            }
            if(rockets.length-k>=3)
            {
                dist = (double) ((data[i][0] - rockets[2][0])*(data[i][0] - rockets[2][0]) + (data[i][1] - rockets[2][1])*(data[i][1] - rockets[2][1]));
                dist = Math.sqrt(dist);
                if (dist <= data[i][2])
                {
                    kill(i);
                    ship.shotdown(2);
                    for (j = i; j< data.length-k-2; j++)
                    {
                        data[j] = data[j + 1];
                    }
                    data[j] = null;
                    rockets[2] = null;
                    k++;
                    continue;
                }
            }
        }
        int[][] refined=new int[data.length-k][4];
        for(j=0;j<refined.length-k;j++) refined[j]=data[j];
        return refined;
    }
    public void kill(int i)
    {
        int j=0;
        if(i>=enemies.length+hostiles.length) return;
        if(i>=enemies.length)
        {
            i-=enemies.length;
            hostiles[i].hit();
            if(hostiles[i].isgone()==true)
            {
                for(j=i;j+1<hostiles.length;j++)
                {
                    hostiles[j]=hostiles[j+1];
                }
                hostiles[j]=null;
                Runner[] data=hostiles;
                hostiles=new Runner[data.length-1];
                for(j=0;j<data.length-1;j++) hostiles[j]=data[j];
            }
        }
        else
        {
            enemies[i].hit();
            if(enemies[i].isgone()==false)
            {
                for(j=i;j+1<enemies.length;j++)
                {
                    enemies[j]=enemies[j+1];
                }
                enemies[j]=null;
                SmallFry[] data=enemies;
                enemies=new SmallFry[data.length-1];
                for(j=0;j<data.length-1;j++) enemies[j]=data[j];
            }
        }
    }
    public void fire() {ship.fire();}
    public void moveship(int j, int i) {ship.movement(j,i);}
    public void shoot() {ship.shoot();}
    class Ship
    {
        private int radius;
        private int crdx;
        private Missile[] rockets;
        private int shot;
        private int velocity;
        Ship()
        {
            crdx=Controller.SIZE;
            radius=25;
            shot=0;
            velocity=0;
            rockets=new Missile[3];
        }
        public int getx() {return crdx;}
        public int getr() {return radius;}
        public int gets() {return  shot;}
        public int[] extract(int k)
        {
            if((shot==0)||(k>=shot)) return null;
            int[] data=new int[4];
            data[0]=rockets[k].getx();
            data[1]=rockets[k].gety();
            data[2]=1;
            data[3]=-1;
            return data;
        }
        public void shotdown(int i)
        {
            if(i>=3) return;
            int j=0;
            for(j=i;j+1<rockets.length;j++)
            {
                rockets[j]=rockets[j+1];
            }
            rockets[j]=null;
            Missile[] data=rockets;
            for(j=0;j<rockets.length-1;j++) rockets[j]=data[j];
            shot--;
        }
        public void movement(int j, int i)
        {
            if (j==-1) velocity=-5;
            else if (j==1) velocity=5;
            else if (j==2) shot++;
            else if (j==0) velocity=0;
            if((velocity==-1)&&(crdx<=radius)) return;
            else if((velocity==1)&&(crdx>=Controller.SIZE)) return;
            else crdx+=velocity*i;
        }
        public void fire()
        {
            if(shot!=0)
            {
                for (int k = 0; k < shot; k++) {rockets[k].movement(1); }
                if (rockets[0].gety() <= 0)
                {
                    if(rockets[1]!=null)
                    {
                        if(rockets[2]!=null)
                        {
                            rockets[0] = rockets[1];
                            rockets[1] = rockets[2];
                            rockets[2] = null;
                        }
                        else
                        {
                            rockets[0]=rockets[1];
                            rockets[1]=null;
                        }
                    }
                    else rockets[0]=null;
                    shot--;
                }
            }
        }
        public void shoot()
        {
            if(shot==3) return;
            rockets[shot]=new Missile(crdx,Controller.SIZE-3*radius+1, false);
            shot++;
        }
    }
    class Missile
    {
        private int crdx;
        private int crdy;
        private boolean hostile;
        private int velocity;
        Missile(int x, int y, boolean b)
        {
            crdx=x;
            crdy=y;
            hostile=b;
            velocity=5;
        }
        public void movement(int i)
        {
            if(hostile==true) crdy+=velocity*i;
            else crdy-=velocity*i;
        }
        public int getx() {return crdx;}
        public int gety() {return crdy;}
    }
    abstract class Attacker
    {
        protected int spritenum;
        protected int crdx;
        protected int crdy;
        protected int radius;
        protected int health;
        protected double weight;
        protected int velocity;
        protected boolean direction;
        protected int limitr;
        protected int limitl;
        Attacker()
        {
            spritenum=1;
            crdx=25;
            crdy=25;
            radius=25;
            health=1;
            weight=0;
            velocity=1;
            limitr=100;
            limitl=0;
            direction=true;
        }
        public int getx() {return crdx;}
        public int gety() {return crdy;}
        public int getr() {return radius;}
        public int getlr() {return limitr;}
        public int getll() {return limitl;}
        public int gets() {return spritenum;}
        public void setx(int x) {crdx=x;}
        public void sety(int y) {crdy=y;}
        public void setlr(int l) {limitr=l;}
        public void setll(int l) {limitl=l;}
        public void hit() {health--;}
        public boolean isgone()
        {
            if(health>=0) return false;
            return true;
        }
        public void movement(int m)
        {
            if(direction==true) crdx+=m*velocity;
            else crdx-=m*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr)) direction=(!direction);
        }
    }
    class SmallFry extends Attacker
    {
        SmallFry()
        {
            spritenum=1;
            crdx=25;
            crdy=25;
            radius=25;
            health=2;
            weight=0.1;
            velocity=1;
            limitr=100;
            limitl=0;
        }
        public void movement(int m)
        {
            if(direction==true) crdx+=m*velocity;
            else crdx-=m*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr))
            {
                direction=(!direction);
                crdy+=50;
            }
        }
    }
    class Runner extends Attacker
    {
        Runner()
        {
            spritenum=2;
            crdx=25;
            crdy=100;
            radius=25;
            health=2;
            weight=0.5;
            velocity=2;
            limitr=100;
            limitl=0;
        }
        public void movement(int m)
        {
            if(direction==true) crdx+=m*velocity;
            else crdx-=m*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr))
            {
                direction=(!direction);

            }
        }
    }
}
