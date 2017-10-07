
package map;

/**
 * @author GuoWei
 */
public class Vector {
    public int x;
    public int y;
    public Vector(int x, int y){
        this.x=x;
        this.y=y;
    }
    public Vector nAdd(Vector v){
        return new Vector(x+v.x, y+v.y);
    }
    public Vector nMultiply(int n){
        return new Vector(x*n, y*n);
    }
    public void add(Vector v){
        this.x+=v.x;
        this.y+=v.y;
    }
    public void multiply(int n){
        x*=n;
        y*=n;
    }
    
    @Override
    public boolean equals(Object object){
        if(object==null)
            return false;
        if(!(object instanceof Vector))
            return false;
        else{
            Vector v=(Vector)object;
            return (x==v.x)&&(y==v.y);
        }
    }
    public String toString(){
        return "("+x+","+y+")";
    }
}
