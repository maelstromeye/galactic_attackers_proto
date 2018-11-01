import java.util.*;
import java.util.zip.CheckedOutputStream;

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
        difficulty=1;
        Runner.weight=0.2;
        SmallFry.weight=0.1;
        ship=new Ship();
        seed=new double[10];
        seed[0]=randfrom(128,256);
        seed[1]=randfrom(seed[0]/2, seed[0]);
        //seed[9]=randfrom((seed[0]>seed[9])?(double)seed[9]:(double)seed[0], (seed[0]>seed[9])?(double)seed[0]:(double)seed[9]);
        seed[2]=randfrom(seed[0]/8, seed[0]/4);
        seed[3]=randfrom(seed[0]/12, seed[0]/8);
        seed[4]=randfrom(seed[0]/16, seed[0]/12);
        seed[9]=randfrom(seed[0]+1, 2*seed[0]+1);
        //seed[9]=randfrom((double)seed[9], (double)seed[0]);
        modifier=seed[0]/seed[1]/seed[2]*107/109;
        if(modifier>0.2) modifier=modifier/2;
        generator();
        freemiss=null;
        queue=new Vector();
    }
    private int randfrom(double min, double max) {return (int) ((Math.random()*Math.abs(max-min))+((min<=max)?min:max));}
    public void generator() {
        int y, x, smallfries, runners, abnormals, thiccboyes, hivewitches;
        int[] quantity = new int[10];
        int rows;
        smallfries = (int) (Math.sqrt(2 * difficulty) / (SmallFry.weight + seed[1] / seed[0] * Runner.weight + seed[2] / seed[0] * Abnormal.weight + seed[3] / seed[0] * Thiccboy.weight + seed[4]/seed[0]*Hivewitch.weight));
        runners = (int) (seed[1] / seed[0] * smallfries);
        abnormals = (int) (seed[2] / seed[0] * smallfries);
        thiccboyes = (int) (seed[3] / seed[0] * smallfries);
        hivewitches= (int) (seed[4]/seed[0]*smallfries);
        System.out.println(seed[0]);
        System.out.println(seed[1]);
        System.out.println(seed[2]);
        System.out.println(seed[3]);
        if (runners > 0)
        {
            rows=runners/10+1;
            for(int j=0;j<rows;j++) quantity[j]=runners/rows;
            for(int j=0;j<runners%rows;j++) quantity[j]++;
            for(int j=0; j<rows;j++)
            {
                x=Controller.SIZE-quantity[j]*Runner.radius;
                y=j*Runner.radius+25;
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Runner(x+i*Runner.radius*2, y, x-5*Runner.radius+i*Runner.radius*2, x+i*Runner.radius*2+5*Runner.radius));
                }
            }
        }
        if(smallfries>0)
        {
            rows=smallfries/10+1;
            for(int j=0;j<rows;j++) quantity[j]=smallfries/rows;
            for(int j=0;j<smallfries%rows;j++) quantity[j]++;
            for(int j=0; j<rows;j++)
            {
                x=Controller.SIZE-quantity[j]*SmallFry.radius;
                y=j*SmallFry.radius+25+(runners/10+1)*Runner.radius;
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
                x = Controller.SIZE - runners/(runners/10+1)*Runner.radius-10*Abnormal.radius;
                y = j*Abnormal.radius*2 + 25 + (runners / 10 + 1) * Runner.radius + (smallfries / 10 + 1) * SmallFry.radius;
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
                y=j*Hivewitch.radius+25+(runners/10+1)*Runner.radius+(smallfries/10+1)*SmallFry.radius;
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
                y=(int) (j*Thiccboy.radius+25+(runners/10+1)*Runner.radius+(smallfries/10+1)*SmallFry.radius+(hivewitches/5+0.8)*Hivewitch.radius);
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Thiccboy(x+i*Thiccboy.radius*2, y, x-10*Thiccboy.radius+i*Thiccboy.radius*2, x+i*Thiccboy.radius*2+10*Thiccboy.radius));
                }
            }
        }
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
        int k=0;
        int[][] data=new int[sumrock()][4];
        for(int i=0; i<list.size(); i++)
        {
            for (int j=0; j<list.get(i).gets(); j++)
            {
                if (list.get(i).extract(j)!=null)
                {
                    data[k][0]=list.get(i).extract(j).getx();
                    data[k][1]=list.get(i).extract(j).gety();
                    data[k][2]=-1;
                    data[k][3]=-2;
                    k++;
                }

            }
        }
        return data;
    }
    public int[][] getMissiles()
    {
        if(ship==null) return null;
        if(ship.gets()==0) return null;
        int[][] data = new int[ship.gets()][4];
        for (int i=0; i<ship.gets(); i++)
        {
            data[i][0]=ship.extract(i).getx();
            data[i][1]=ship.extract(i).gety();
            data[i][2]=1;
            data[i][3]=-1;
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
            ship.fire(1);
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
    public void hit()
    {
        if(pause==true) return;
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
        if(i<=0) return;
        rockets=getRockets();
        if(ship!=null)
        {
            for(int j=0; j<i; j++)
            {
                if(Math.sqrt(((ship.getx()-rockets[j][0])*(ship.getx()-rockets[j][0])+(Ship.HEIGHT-rockets[j][1])*(Ship.HEIGHT-rockets[j][1])))<=ship.getr())
                {
                    lives--;
                    pause = true;
                    ship = null;
                    for (Attacker a : list) a.reload();
                    freemiss=null;
                    break;
                }
            }
            if(freemiss!=null)
            {
                for(int j=0; j<freemiss.size(); j++)
                {
                    if (Math.sqrt((ship.getx()-freemiss.get(j).getx())*(ship.getx()-freemiss.get(j).getx())+(Ship.HEIGHT-freemiss.get(j).gety())*(Ship.HEIGHT-freemiss.get(j).gety()))<=ship.getr())
                    {
                        lives--;
                        pause = true;
                        ship = null;
                        for (Attacker a : list) a.reload();
                        freemiss=null;
                        break;
                    }
                }
            }
        }
        return;
    }
    private void append(Missile m)
    {
        if(m==null) return;
        if(freemiss==null) freemiss=new Vector();
        freemiss.add(m);
    }
    public int[][] getfree()
    {
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
    public void pause() {pause=!pause; if(ship==null) ship=new Ship();}
    public void moveship(int i, int j) {if(pause==true) return; else if(ship!=null) ship.movement(i, j);}
    public void shoot(int i) {if(pause==true) pause(); else if(ship!=null) {ship.shoot(i); ship.movement(1, 4);}}
    public int sumrock() {int i=0; for(Attacker a:list) i+=a.gets(); return i;}
    static class Ship
    {
        private int radius;
        private int crdx;
        private Missile[] rockets;
        private int shot;
        private int velocity;
        private int counter;
        private int memory;
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
            rockets=new Missile[3];
            counter=0;
            left=false;
            right=false;
        }
        public int getx() {return crdx;}
        public int getr() {return radius;}
        public int gets() {return  shot;}
        public void reload() {rockets=new Missile[3]; shot=0;}
        public Missile extract(int k)
        {
            if((shot==0)||(k>=shot)) return null;
            return rockets[k];
        }
        public void shotdown(int i)
        {
            if(i>=3) return;
            int j;
            for(j=i;j+1<rockets.length;j++)
            {
                rockets[j]=rockets[j+1];
            }
            rockets[j]=null;
            Missile[] data=rockets;
            rockets=new Missile[3];
            for(j=0;j<3;j++) rockets[j]=data[j];
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
            }
        }
        public void fire(int i)
        {
            if(shot!=0)
            {
                for (int k = 0; k < shot; k++) {rockets[k].movement(i); }
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
        public void shoot(int i)
        {
            if(i==1) counter=1;
            else if(i==0)
            {
                counter=0;
                return;
            }
            if(shot==3) return;
            if(counter%20==1)
            {
                rockets[shot] = new Missile(crdx, HEIGHT + 1, false);
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
            weight=0.5;
        }
        Abnormal(int x, int y, int ll, int lr, boolean b)
        {
            shot=0;
            rockets=new Missile[1];
            direction=true;
            spritenum = 3;
            crdx = x;
            crdy = y;
            health = 2;
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
            if(magazine==100) shoot();
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
        public void reload() {shot=0; rockets=new Missile[1];}
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
            magazine+=1;
            if(magazine>=150) shoot();
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
        public void reload() {shot=0; rockets=new Missile[4];}
    }
    static class Hivewitch extends Attacker
    {
        private int magazine;
        public static double weight;
        private int mandate;
        static
        {
            radius=40;
            weight=2.4;
        }
        Hivewitch(int x, int y, int ll, int lr)
        {
            magazine=0;
            rockets=null;
            direction=true;
            spritenum=5;
            crdx=x;
            crdy=y;
            health=15;
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
}
