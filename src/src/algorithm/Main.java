/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;
import java.util.LinkedList;
import map.*;
import robot.*;
import simulation.*;
/**
 *
 * @author WGUO002
 */
public class Main {
    public static void main(String[] args){
        LinkedList<Vector> obstacle=new LinkedList<>();
        obstacle.add(new Vector(15,7));
        obstacle.add(new Vector(14,7));
        obstacle.add(new Vector(13,7));
        obstacle.add(new Vector(16,7));
        obstacle.add(new Vector(16,8));
        obstacle.add(new Vector(16,9));
        obstacle.add(new Vector(16,10));
        obstacle.add(new Vector(15,10));
        obstacle.add(new Vector(14,10));
        obstacle.add(new Vector(13,10));
        Map map=new Map(obstacle);
        Simulator simulator=new Simulator(map);
        Algorithm algo=new Algorithm(simulator);
        algo.explore(1000, 101);
//        algo.getMap().printMap();
        Robot robot = new Robot(true);
        ShortestPath sp = new ShortestPath(algo.getMap(), robot);
        sp.executeShortestPath(18, 13);
    }
}
