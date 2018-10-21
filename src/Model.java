class Model
{
    private int difficulty;
    private int lives;
    private final int SIZE;
    private SmallFry[] enemies;
    Model(int s)
    {
        SIZE=s;
        lives=3;
        difficulty=1;
        enemies=new SmallFry[10];
        this.generator();
    }
    public void generator() {
        for (int i = 0; i < 10; i++)
        {
            enemies[i] = new SmallFry();
            enemies[i].setx(25+i*50);
        }
    }
    public int[][] getPositions()
    {
        int[][] data=new int[3][enemies.length];
        for(int i=0; i<enemies.length; i++)
        {
            data[0][i]=enemies[i].getx();
            data[1][i]=enemies[i].gety();
            data[2][i]=enemies[i].getr();
        }
        return data;
    }
    public void movement(int i)
    {
        for(SmallFry fry:enemies) fry.movement(i, SIZE);
    }
    class Ship
    {
        private int radius;
        private int crdx;
        private int shot;
        private int velocity;
    }
    abstract class Attacker
    {
        protected int crdx;
        protected int crdy;
        protected int radius;
        protected int health;
        protected double weight;
        protected int velocity;
        protected boolean direction;
        Attacker()
        {
            crdx=25;
            crdy=25;
            radius=25;
            health=1;
            weight=0;
            velocity=1;
            direction=true;
        }
        public int getx() {return crdx;}
        public int gety() {return crdy;}
        public int getr() {return radius;}
        public void setx(int x) {crdx=x;}
        public void sety(int y) {crdy=y;}
        public void movement(int m, int limit)
        {
            if(direction==true) crdx+=m*velocity;
            else crdx-=m*velocity;
            if ((crdx-radius==0)||(crdx+radius==limit)) direction=(!direction);
        }
    }
    class SmallFry extends Attacker
    {
        SmallFry()
        {
            crdx=25;
            crdy=25;
            radius=25;
            health=1;
            weight=0.1;
            velocity=1;
        }
        public void movement(int m, int limit)
        {
            if(direction==true) crdx+=m*velocity;
            else crdx-=m*velocity;
            if ((crdx-radius==0)||(crdx+radius==limit))
            {
                direction=(!direction);
                crdy+=50;
            }
        }
    }
}
