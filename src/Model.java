import java.util.*;
import java.util.concurrent.atomic.LongAccumulator;

class Model
{
    private boolean pause;
    private int difficulty;
    private int lives;
    private Ship ship;
    private double[] seed;
    private static List<Attacker> list;
    private double modifier;
    private List<Missile> freemiss;
    private static List<Attacker> queue;
    Model()
    {
        pause=false;
        list=new Vector();
        lives=3;
        difficulty=5;
        Runner.weight=0.2;
        SmallFry.weight=0.1;
        ship=new Ship();
        seed=new double[10];
        seed[0]=randfrom(128,256);
        seed[1]=randfrom(seed[0]/2, seed[0]*2);
        //seed[9]=randfrom((seed[0]>seed[9])?(double)seed[9]:(double)seed[0], (seed[0]>seed[9])?(double)seed[0]:(double)seed[9]);
        seed[2]=randfrom(seed[0]/3, seed[0]/3*2);
        seed[3]=randfrom(seed[0]/8, seed[0]/4);
        seed[4]=randfrom(seed[0]/16, seed[0]/8);
        seed[5]=randfrom(seed[0]/4, seed[0]/2);
        seed[9]=randfrom(seed[0]+1, 2*seed[0]+1);
        //seed[9]=randfrom((double)seed[9], (double)seed[0]);
        modifier=seed[0]/seed[1]/seed[2]*107/109;
        if(modifier>0.2) modifier=modifier/2;
        generator();
        freemiss=null;
        queue=new Vector();
    }
    private double randfrom(double min, double max) {return (Math.random()*Math.abs(max-min))+((min<=max)?min:max);}
    private double evolve() {seed[8]=(seed[8]*(seed[0]*947*109/107)+(seed[0]*947*109/107))%seed[0]+seed[0]/2; return seed[0]/seed[8];}
    public void generator()
    {
        int y, x, smallfries, runners, abnormals, thiccboyes, hivewitches, laserboys;
        double d,e=0;
        int[] quantity = new int[10];
        int rows;
        smallfries = (int) (Math.sqrt(2*difficulty) / (SmallFry.weight + seed[1] / seed[0] * Runner.weight + seed[2] / seed[0] * Abnormal.weight + seed[3] / seed[0] * Thiccboy.weight + seed[4]/seed[0]*Hivewitch.weight + seed[5]/seed[0]*Laserboy.weight));
        runners = (int) (seed[1]/seed[0]);
        abnormals = (int) (seed[2]/ seed[0]);
        thiccboyes = (int) (seed[3]/seed[0]);
        hivewitches= (int) (seed[4]/seed[0]);
        laserboys= (int) (seed[5]/seed[0]);
        d=smallfries*SmallFry.weight+runners*Runner.weight+abnormals*Abnormal.weight+thiccboyes*Thiccboy.weight+hivewitches*Hivewitch.weight+laserboys* Laserboy.weight;
        while(d>e)
        {
            runners=(int) (seed[1]/seed[0]*smallfries*evolve());
            abnormals = (int) (seed[2]/seed[0]*smallfries*evolve());
            thiccboyes = (int) (seed[3]/seed[0]*smallfries*evolve());
            hivewitches = (int) (seed[4]/seed[0]*smallfries*evolve());
            laserboys = (int) (seed[5]/seed[0]*smallfries*evolve());
            e=smallfries*SmallFry.weight+runners*Runner.weight+abnormals*Abnormal.weight+thiccboyes*Thiccboy.weight+hivewitches*Hivewitch.weight+laserboys* Laserboy.weight;
        }
        System.out.println(seed[0]);
        System.out.println(seed[1]);
        System.out.println(seed[2]);
        System.out.println(seed[3]);
        if(laserboys>0)
        {
            rows=(int) ((double) laserboys/6+0.84);
            for (int j = 0; j < rows; j++) quantity[j] = laserboys / rows;
            for (int j = 0; j < laserboys % rows; j++) quantity[j]++;
            for (int j = 0; j < rows; j++)
            {
                x = Controller.SIZE-quantity[j]*Laserboy.radius+2*Laserboy.radius;
                y =  j*Laserboy.radius + 25;
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Laserboy(x+((2*(i%6)+(i%6+1-(i+1)%2)/2)%6-2)*Laserboy.radius*2, y, x+((2*(i%6)+(i%6+1-(i+1)%2)/2)%6-2)*Laserboy.radius*2+(((2*(i%6)+(i%6+1-(i+1)%2)/2)%6-2<=0)?(-10*Laserboy.radius):(-Laserboy.radius)), x+((2*(i%6)+(i%6+1-(i+1)%2)/2)%6-2)*Laserboy.radius*2+((((2*(i%6)+(i%6+1-(i+1)%2)/2)%6-2)<=0)?(Laserboy.radius):(10*Laserboy.radius)), (((2*(i%6)+(i%6+1-(i+1)%2)/2)%6-2)<=0)?false:true));
                }
            }
        }
        if (runners>0)
        {
            rows=(int) ((double) runners/10+0.9);
            for(int j=0;j<rows;j++) quantity[j]=runners/rows;
            for(int j=0;j<runners%rows;j++) quantity[j]++;
            for(int j=0; j<rows;j++)
            {
                x=Controller.SIZE-quantity[j]*Runner.radius;
                y=(int) ((double) laserboys/6+0.84)*Laserboy.radius*2 +j*Runner.radius+25;
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Runner(x+i*Runner.radius*2, y, x-5*Runner.radius+i*Runner.radius*2, x+i*Runner.radius*2+5*Runner.radius));
                }
            }
        }
        if(smallfries>0)
        {
            rows=(int) ((double) smallfries/10+0.9);
            for(int j=0;j<rows;j++) quantity[j]=smallfries/rows;
            for(int j=0;j<smallfries%rows;j++) quantity[j]++;
            for(int j=0; j<rows;j++)
            {
                x=Controller.SIZE-quantity[j]*SmallFry.radius;
                y=(int) (j*SmallFry.radius+25+((double) runners/10+0.9)*Runner.radius+(int)((double) laserboys/6+0.84)*Laserboy.radius*2);
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new SmallFry(x+i*SmallFry.radius*2, y, x-10*SmallFry.radius+i*SmallFry.radius*2, x+i*SmallFry.radius*2+10*SmallFry.radius));
                }
            }
        }
        if(abnormals>0)
        {
            rows=(int) ((double) abnormals/2+0.5);
            for (int j = 0; j < rows; j++) quantity[j] = abnormals / rows;
            for (int j = 0; j < abnormals % rows; j++) quantity[j]++;
            for (int j = 0; j < rows; j++)
            {
                x = (int) (Controller.SIZE - runners/((double) runners / 10 + 0.9)*Runner.radius-10*Abnormal.radius);
                y = (int) (j*Abnormal.radius*2 + 25 + ((double) runners / 10 + 0.9) * Runner.radius + ((double) smallfries / 10 + 0.9) * SmallFry.radius);
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Abnormal(x+i*(Controller.SIZE-x)*2, y, x+i*(Controller.SIZE-x)*2+((i==0)?(-5*Abnormal.radius):(-30*Abnormal.radius)), x+i*(Controller.SIZE-x)*2+((i==0)?30*Abnormal.radius:5*Abnormal.radius), (i==0)?true:false));
                }
            }
        }
        if(hivewitches>0)
        {
            rows=(int) ((double) hivewitches/5+0.8);
            for(int j=0;j<rows;j++) quantity[j]=hivewitches/rows;
            for(int j=0;j<hivewitches%rows;j++) quantity[j]++;
            for(int j=0; j<rows;j++)
            {
                x=Controller.SIZE-quantity[j]*Hivewitch.radius;
                y=(int) (j*Hivewitch.radius+25+((double) runners/10+0.9)*Runner.radius+((double) smallfries/10+0.9)*SmallFry.radius+(int) ((double) laserboys/6+0.84)*Laserboy.radius*2);
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Hivewitch(x+i*Hivewitch.radius*2, y, x-10*Hivewitch.radius+i*Hivewitch.radius*2, x+i*Hivewitch.radius*2+10*Hivewitch.radius));
                }
            }
        }
        if(thiccboyes>0)
        {
            rows=(int) ((double) thiccboyes/5+0.8);
            for(int j=0;j<rows;j++) quantity[j]=thiccboyes/rows;
            for(int j=0;j<thiccboyes%rows;j++) quantity[j]++;
            for(int j=0; j<rows;j++)
            {
                x=Controller.SIZE-quantity[j]*Thiccboy.radius;
                y=(int) (j*Thiccboy.radius+25+((double) runners/10+0.9)*Runner.radius+((double) smallfries/10+0.9)*SmallFry.radius+((double) hivewitches/5+0.8)*Hivewitch.radius+(int)((double) laserboys/6+0.84)*Laserboy.radius*2);
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Thiccboy(x+i*Thiccboy.radius*2, y, x-10*Thiccboy.radius+i*Thiccboy.radius*2, x+i*Thiccboy.radius*2+10*Thiccboy.radius));
                }
            }
        }
        /*if(laserboys>0)
        {
            rows=(int) ((double) laserboys/4+0.8);
            for (int j = 0; j < rows; j++) quantity[j] = laserboys / rows;
            for (int j = 0; j < laserboys % rows; j++) quantity[j]++;
            for (int j = 0; j < rows; j++)
            {
                x = (int) (Controller.SIZE - runners/((double) runners/10+0.9)*Runner.radius-10*Laserboy.radius);
                y = (int) (j*Laserboy.radius*2 + 25 - ((double) runners / 10 + 0.9) * Runner.radius + ((double) smallfries / 10 + 0.9) * SmallFry.radius);
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Laserboy(x+(i%2)*(Controller.SIZE-x)*2+(((i+1)%3)-1)*Laserboy.radius*2, y, x+(i%2)*(Controller.SIZE-x)*2+(((i+1)%3)-1)*Laserboy.radius*2+(((i%2)==0)?(-7*Laserboy.radius):(-13*Laserboy.radius)), x+(i%2)*(Controller.SIZE-x)*2+(((i+1)%3)-1)*Laserboy.radius*2+(((i%2)==0)?(13*Laserboy.radius):(7*Laserboy.radius)), ((i%2)==0)?false:true));
                }
            }
        }*/
    }
    /*private <T extends Attacker> void fillout(int number, T t, int index)
    {
        int[] quantity=new int[5];
        int y, x, rows=number/10+1;
        for(int j=0;j<rows;j++) quantity[j]=number/rows;
        for(int j=0;j<number%rows;j++) quantity[j]++;
        for(int j=0; j<rows;j++)
        {
            x=Controller.SIZE-quantity[j]*T.radius;
            y=j*T.radius+25;
            for (int i = 0; i < quantity[j]; i++)
            {
                list.add(index, new Abnormal(x+i*T.radius*2, y, x-5*T.radius+i*T.radius*2, x+i*T.radius*2+5*T.radius));
                index++;
            }
        }
    }*/
    public int[][] getPositions()
    {
        if(pause) return null;
        int[][] data=new int[list.size()+((ship==null)?0:1)][4];
        for(int i=0; i<list.size(); i++)
        {
            data[i][0]=list.get(i).getx();
            data[i][1]=list.get(i).gety();
            data[i][2]=list.get(i).radius;
            data[i][3]=list.get(i).getspr();
        }
        if(ship==null) return data;
        data[list.size()][0]=ship.getx();
        data[list.size()][1]=Ship.HEIGHT;
        data[list.size()][2]=ship.getr();
        data[list.size()][3]=100;
        return data;
    }
    public int[][] getRockets()
    {
        if(pause) return null;
        int k=0;
        Missile missile;
        int[][] data=new int[sumrock()][4];
        for(int i=0; i<list.size(); i++)
        {
            for (int j=0; j<list.get(i).gets(); j++)
            {
                if (list.get(i).extract(j)!=null)
                {
                    missile=list.get(i).extract(j);
                    data[k][0]=missile.getx();
                    data[k][1]=missile.gety();
                    data[k][2]=1;
                    if(missile.gety()==Ship.HEIGHT) data[k][3]=-500;
                    else data[k][3]=-2;
                    k++;
                }
            }
        }
        return data;
    }
    public int[][] getMissiles()
    {
        if(pause) return null;
        if(ship==null) return null;
        if(ship.gets()==0) return null;
        Missile missile;
        int[][] data = new int[ship.gets()][4];
        for (int i=0; i<ship.gets(); i++)
        {
            missile=ship.extract(i);
            data[i][0]=missile.getx();
            data[i][1]=missile.gety();
            data[i][2]=1;
            data[i][3]=-1;
        }
        return data;
    }
    public int[][] getfree()
    {
        if(pause) return null;
        if(freemiss==null) return null;
        int[][] data=new int[freemiss.size()][4];
        for(int i=0; i<freemiss.size(); i++)
        {
            data[i][0]=freemiss.get(i).getx();
            data[i][1]=freemiss.get(i).gety();
            data[i][2]=1;
            data[i][3]=-2;
        }
        return data;
    }
    public void movement(int i)
    {
        if(pause==true) return;
        for(int j=0;j<list.size();j++) list.get(j).movement(i);
        for(Attacker a:list)
        {
            modifier=((seed[0]*947*109/107)*modifier+seed[0]/seed[9]*107/109)%100;
            System.out.println(modifier);
            //System.out.println(modifier+Math.log(Controller.SIZE-a.gety()));
            if(modifier+Math.log(Controller.SIZE-a.gety())>106.3) a.shoot();
        }
        for(int j=0; j<queue.size(); j++) list.add(queue.get(j));
        queue.clear();
        if(freemiss!=null)
        {
            for(Missile m:freemiss)
            {
                if(m.getv()==true) m.vertmovement(1);
                else m.movement(1);
            }
        }
        if(ship!=null)
        {
            ship.movement(1,4);
            ship.shoot(2);
        }
    }
    public boolean checkwin()
    {
        if(list.isEmpty()==true)
        {
            difficulty+=2;
            ship.reload();
            this.generator();
            return true;
        }
        else return false;
    }
    public void skip()
    {
        list.clear();
        difficulty+=5;
        if(ship!=null) ship.reload();
        else ship=new Ship();
        this.generator();
    }
    public boolean hit()
    {
        if(pause==true) return false;
        int[][] rockets=getMissiles();
        if (rockets!=null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (rockets == null) break;
                for (int j = 0; j < rockets.length; j++)
                {
                    if (Math.sqrt(((list.get(i).getx() - rockets[j][0]) * (list.get(i).getx() - rockets[j][0]) + (list.get(i).gety() - rockets[j][1]) * (list.get(i).gety() - rockets[j][1]))) <= list.get(i).getr())
                    {
                        list.get(i).hit();
                        if (list.get(i).isgone()==true)
                        {
                            for(int k=0; k<list.get(i).gets(); k++) append(list.get(i).extract(k));
                            list.remove(i);
                        }
                        ship.shotdown(j);
                        rockets = getMissiles();
                        break;
                    }
                }
            }
        }
        int i=sumrock();
        if(i<=0) return false;
        rockets=getRockets();
        if(ship!=null)
        {
            for(int j=0; j<i; j++)
            {
                if(Math.sqrt((ship.getx()-rockets[j][0])*(ship.getx()-rockets[j][0])+(Ship.HEIGHT-rockets[j][1])*(Ship.HEIGHT-rockets[j][1]))<=ship.getr())
                {
                    return true;
                }
            }
            if(freemiss!=null)
            {
                for(int j=0; j<freemiss.size(); j++)
                {
                    if (Math.sqrt((ship.getx()-freemiss.get(j).getx())*(ship.getx()-freemiss.get(j).getx())+(Ship.HEIGHT-freemiss.get(j).gety())*(Ship.HEIGHT-freemiss.get(j).gety()))<=ship.getr())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private void append(Missile m)
    {
        if(m==null) return;
        if(freemiss==null) freemiss=new Vector();
        freemiss.add(m);
    }
    public void shotdown()
    {
        lives--;
        ship=null;
        for (Attacker a : list) a.reload();
        freemiss=null;
    }
    public void pause() {pause=!pause; if(ship==null) ship=new Ship(); else if(pause==false) ship.fullstop();}
    public boolean getpause() {return pause;}
    public void moveship(int i, int j) {if(pause==true) return; else if(ship!=null) ship.movement(i, j);}
    public void shoot(int i) {if(pause==true&&i!=0) {pause(); return;} else if(ship!=null) {ship.shoot(i); ship.movement(1, 4);}}
    public int sumrock() {int i=0; for(Attacker a:list) i+=a.gets(); return i;}
    public boolean islive() {if(lives>0) return true; return false;}
    static class Ship
    {
        private int radius;
        private int crdx;
        private List<Missile> rockets;
        private int shot;
        private int velocity;
        private int counter;
        private boolean left;
        private boolean right;
        public static int HEIGHT;
        static {HEIGHT=Controller.SIZE-75;}
        Ship()
        {
            crdx=Controller.SIZE;
            radius=10;
            shot=0;
            velocity=0;
            rockets=new Vector();
            counter=0;
            left=false;
            right=false;
        }
        public int getx() {return crdx;}
        public int getr() {return radius;}
        public int gets() {return  shot;}
        public void reload() {rockets.clear(); shot=0;}
        public void fullstop() {left=false; right=false; velocity=0; counter=0;}
        public Missile extract(int k)
        {
            if((shot==0)||(k>=shot)) return null;
            return rockets.get(k);
        }
        public void shotdown(int i)
        {
            if(i>=shot) return;
            rockets.remove(i);
            shot--;
        }
        public void movement(int i, int j)
        {
            if(j==1&&left==true)
            {
                velocity=0;
                right=true;
                return;
            }
            if(j==-1&&right==true)
            {
                velocity=0;
                left=true;
                return;
            }
            if (j==-1)
            {
                left=true;
                velocity=-4;
            }
            else if(j==1)
            {
                right=true;
                velocity=4;
            }
            else if(j==-2)
            {
                if(right==true) velocity=4;
                else velocity=0;
                left=false;
            }
            else if(j==2)
            {
                if(left==true) velocity=-4;
                else velocity=0;
                right=false;
            }
            else if(j==4)
            {
                if(counter!=0) counter++;
                if ((velocity <= 0) && (crdx <= radius)) return;
                else if ((velocity >= 0) && (crdx >= Controller.SIZE * 2 - radius * 3)) return;
                else crdx += velocity * i;
                for (int k=0; k<shot; k++)
                {
                    rockets.get(k).movement(i);
                    if(rockets.get(k).gety()<=0)
                    {
                        rockets.remove(k);
                        shot--;
                        continue;
                    }
                }
            }
        }
        public void shoot(int i)
        {
            if(i==1) {if(counter%30==0) counter=1;}
            else if(i==0)
            {
                counter=0;
                return;
            }
            if(counter%30==1)
            {
                rockets.add(new Missile(crdx, HEIGHT + 1, false));
                counter++;
                shot++;
            }
        }
    }
    static class Missile
    {
        private int crdx;
        private int crdy;
        private boolean hostile;
        private int velocity;
        private int counter;
        private boolean isvert;
        private boolean xdir;
        Missile(int x, int y, boolean b)
        {
            crdx=x;
            crdy=y;
            hostile=b;
            velocity=5;
            counter=0;
        }
        public void movement(int i)
        {
            if(hostile==true) crdy+=velocity*i;
            else crdy-=velocity*i;
        }
        public void vertmovement(int i)
        {
            counter+=i;
            if(counter==2)
            {
                if(hostile==true) crdy+=velocity*i;
                else crdy-=velocity*i;
                counter=0;
                if(xdir==true) crdx+=i*velocity;
                else crdx-=i*velocity;
            }
        }
        public int getx() {return crdx;}
        public int gety() {return crdy;}
        public boolean getxd() {return xdir;}
        public boolean getv() {return isvert;}
        public void isvert() {isvert=true;}
        public void setxd(boolean b) {xdir=b;}
    }
    abstract static class Attacker
    {
        protected Missile[] rockets;
        protected int spritenum;
        protected int crdx;
        protected int crdy;
        public static int radius;
        protected int health;
        protected double velocity;
        protected boolean direction;
        protected int limitr;
        protected int limitl;
        protected int shot;
        Attacker() {}
        public int getx() {return crdx;}
        public int gety() {return crdy;}
        public int getr() {return radius;}
        public int getlr() {return limitr;}
        public int getll() {return limitl;}
        public int gets() {return shot;}
        public int getspr() {return spritenum;}
        public void setx(int x) {crdx=x;}
        public void sety(int y) {crdy=y;}
        public void setlr(int l) {limitr=l;}
        public void setll(int l) {limitl=l;}
        public void hit() {health--;}
        public abstract void reload();
        public boolean isgone()
        {
            if(health>0) return false;
            return true;
        }
        public void movement(int i)
        {
            if(direction==true) crdx+=i*velocity;
            else crdx-=i*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr)) direction=(!direction);
            if(shot==0) return;
            for(Missile m:rockets)
            {
                m.movement(i);
                if((m.gety()>=Controller.SIZE)&&(shot>=rockets.length))
                {
                    reload();
                    return;
                }
            }
        }
        public void shoot()
        {
            if(shot>=rockets.length) return;
            rockets[shot]=new Missile(crdx, crdy, true);
            shot++;
        }
        public Missile extract(int k)
        {
            if((shot==0)||(k>=shot)) return null;
            return rockets[k];
        }
    }
    static class SmallFry extends Attacker
    {
        public static double weight;
        static
        {
            radius=25;
            weight=0.1;
        }
        SmallFry(int x, int y, int ll, int lr)
        {
            shot=0;
            rockets=new Missile[1];
            direction=true;
            spritenum=1;
            crdx=x;
            crdy=y;
            health=1;
            velocity=1;
            limitr=lr;
            limitl=ll;
        }
        public void movement(int i)
        {
            if(direction==true) crdx+=i*velocity;
            else crdx-=i*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr))
            {
                direction=(!direction);
                crdy+=50;
            }
            if(shot==0) return;
            for(Missile m:rockets)
            {
                m.movement(i);
                if((m.gety()>=Controller.SIZE)&&(shot>=rockets.length))
                {
                    reload();
                    return;
                }
            }
        }
        public void reload() {shot=0; rockets=new Missile[1];}
    }
    static class Runner extends Attacker
    {
        public static double weight;
        static
        {
            radius=20;
            weight=0.2;
        }
        Runner(int x, int y, int ll, int lr)
        {
            shot=0;
            rockets=new Missile[2];
            direction=true;
            spritenum=2;
            crdx=x;
            crdy=y;
            health=1;
            velocity=2;
            limitr=lr;
            limitl=ll;
        }
        public void movement(int i)
        {
            if(direction==true) crdx+=i*velocity;
            else crdx-=i*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr))
            {
                direction=(!direction);
            }
            if(shot==0) return;
            for(int j=0; j<shot; j++)
            {
                rockets[j].movement(i);
                if((rockets[j].gety()>=Controller.SIZE)&&(shot>=rockets.length))
                {
                    rockets[j]=rockets[j+1];
                    rockets[j+1]=null;
                    shot--;
                    return;
                }
            }
        }
        public void reload() {shot=0; rockets=new Missile[2];}
    }
    static class Abnormal extends Attacker
    {
        public static double weight;
        private int counter;
        private int magazine;
        static
        {
            radius=25;
            weight=0.33;
        }
        Abnormal(int x, int y, int ll, int lr, boolean b)
        {
            shot=0;
            rockets=new Missile[1];
            direction=true;
            spritenum = 3;
            crdx = x;
            crdy = y;
            health = 3;
            velocity = 1;
            limitr = lr;
            limitl = ll;
            counter=0;
            magazine=0;
            direction=b;
        }
        public void movement(int i)
        {
            counter+=i;
            if(counter==10)
            {
                crdy++;
                counter=0;
            }
            if (direction == true) crdx+=i*velocity;
            else crdx-=i*velocity;
            if ((crdx - radius <= limitl) || (crdx + radius >= limitr))
            {
                direction = (!direction);
                crdy+=radius*3;
            }
            magazine+=i;
            if(magazine>=50) shoot();
            if(shot==0) return;
            for(Missile m:rockets)
            {
                m.vertmovement(i);
                if((m.gety()>=Controller.SIZE)&&(shot>=rockets.length))
                {
                    reload();
                    return;
                }
            }
        }
        public void shoot()
        {
            magazine=0;
            if(shot>=rockets.length) return;
            rockets[shot]=new Missile(crdx, crdy, true);
            rockets[shot].isvert();
            rockets[shot].setxd(direction);
            shot++;
        }
        public void reload() {shot=0; rockets=new Missile[1]; magazine=0;}
    }
    static class Thiccboy extends Attacker
    {
        private int magazine;
        public static double weight;
        static
        {
            radius=40;
            weight=1.2;
        }
        Thiccboy(int x, int y, int ll, int lr)
        {
            magazine=0;
            shot=0;
            rockets=new Missile[4];
            direction=true;
            spritenum=4;
            crdx=x;
            crdy=y;
            health=10;
            velocity=1;
            limitr=lr;
            limitl=ll;
        }
        public void movement(int i)
        {
            if(direction==true) crdx+=i*velocity;
            else crdx-=i*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr))
            {
                direction=(!direction);
                crdy+=50;
            }
            if(shot==0) return;
            magazine+=i;
            if(magazine>=50) shoot();
            if(shot==0) return;
            for(Missile m:rockets)
            {
                m.movement(i);
                if((m.gety()>=Controller.SIZE)&&(shot>=rockets.length))
                {
                    reload();
                    return;
                }
            }
        }
        public void shoot()
        {
            magazine=0;
            if(shot>=rockets.length) return;
            rockets[0]=new Missile(crdx+radius/4, crdy+radius, true);
            rockets[1]=new Missile(crdx-radius/4, crdy+radius, true);
            rockets[2]=new Missile(crdx-radius/4, crdy, true);
            rockets[3]=new Missile(crdx+radius/4, crdy, true);
            shot+=4;
        }
        public void reload() {shot=0; rockets=new Missile[4]; magazine=0;}
    }
    static class Hivewitch extends Attacker
    {
        private int magazine;
        public static double weight;
        private int mandate;
        static
        {
            radius=40;
            weight=2;
        }
        Hivewitch(int x, int y, int ll, int lr)
        {
            magazine=0;
            rockets=null;
            direction=true;
            spritenum=5;
            crdx=x;
            crdy=y;
            health=10;
            velocity=1;
            limitr=lr;
            limitl=ll;
            mandate=200;
        }
        public void movement(int i)
        {
            if(direction==true) crdx+=i*velocity;
            else crdx-=i*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr))
            {
                direction=(!direction);
            }
            mandate-=i;
            magazine+=i;
            if(magazine>=400) shoot();
        }
        public void shoot()
        {
            if(mandate>0) return;
            mandate=200;
            magazine=0;
            queue.add(new SmallFry(crdx+radius*2, crdy+radius*2, (0>crdx-SmallFry.radius*10)?0:crdx-SmallFry.radius*10, (Controller.SIZE*2<crdx+SmallFry.radius*10)?Controller.SIZE*2:crdx+SmallFry.radius*10));
            queue.add(new SmallFry(crdx-radius*2, crdy+radius*2, (0>crdx-SmallFry.radius*10)?0:crdx-SmallFry.radius*10, (Controller.SIZE*2<crdx+SmallFry.radius*10)?Controller.SIZE*2:crdx+SmallFry.radius*10));
        }
        public void reload() {}
    }
    static class Laserboy extends Attacker
    {
        private int magazine, counter, countdown;
        public static double weight;
        static
        {
            radius=30;
            weight=0.75;
        }
        Laserboy(int x, int y, int ll, int lr, boolean b)
        {
            magazine=1;
            rockets=null;
            direction=b;
            spritenum=6;
            crdx=x;
            crdy=y;
            health=5;
            velocity=1;
            limitr=lr;
            limitl=ll;
            counter=0;
            countdown=0;
        }
        public void movement(int i)
        {
            if(direction==true) crdx+=i*velocity;
            else crdx-=i*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr))
            {
                direction=(!direction);
                crdy+=50;
            }
            if(magazine==0) counter+=i;
            else magazine+=i;
            if(magazine>=201) shoot();
            if(countdown!=0) countdown+=i;
            if(countdown>=101)
            {
                spritenum=-66;
                countdown=0;
                magazine=0;
                shot=3;
            }
            if(counter>=150)
            {
                magazine=1;
                counter=0;
                spritenum=6;
                shot=0;
            }
        }
        public void shoot()
        {
            if(magazine!=0)
            {
                spritenum=-6;
                if(countdown==0) countdown=1;
            }
        }
        public Missile extract(int k)
        {
            if((shot==0)||(k>=shot)) return null;
            return new Missile(crdx+(k-1)*radius/2, Ship.HEIGHT, true);
        }
        public void reload() {magazine=1; countdown=0; counter=0; spritenum=6; shot=0;}
    }
}
