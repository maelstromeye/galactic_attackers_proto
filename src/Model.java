import java.awt.event.KeyEvent;

class Model
{
    private int difficulty;
    private int lives;
    private Ship ship;
    private SmallFry[] enemies;
    private Runner[] hostiles;
    Model()
    {
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
        data[enemies.length+hostiles.length][3]=0;
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
    public void moveship(int j, int i) {ship.movement(j,i);}
    public void shootship() {ship.shoot();}
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
        public void movement(int j, int i)
        {
            if(shot!=0)
            {
                for (int k = 0; k < rockets.length; k++)
                {
                    rockets[k].movement(1);
                    if (rockets[k].gety() <= 0)
                    {
                        rockets[k] = null;
                        shot--;
                    }
                }
            }
            if (j==-1) velocity=-5;
            else if (j==1) velocity=5;
            else if (j==2) shot++;
            else if (j==0) velocity=0;
            if((velocity==-1)&&(crdx<=radius)) return;
            else if((velocity==1)&&(crdx>=Controller.SIZE)) return;
            else crdx+=velocity*i;
        }
        public void shoot()
        {
            if(shot==3) return;
            rockets[shot]=new Missile(3*radius+1);
            shot++;
        }
    }
    class Missile
    {
        private int crdx;
        private int crdy;
        private boolean hostile;
        private int velocity;
        Missile(int y)
        {
            crdx=Controller.SIZE;
            crdy=y;
            hostile=false;
            velocity=1;
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
            health=1;
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
            health=1;
            weight=0.5;
            velocity=2;
            limitr=100;
            limitl=0;
        }
    }
}
