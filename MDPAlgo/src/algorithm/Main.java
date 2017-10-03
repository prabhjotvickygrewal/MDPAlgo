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
        obstacle.add(new Vector(7,15));
        obstacle.add(new Vector(7,14));
        obstacle.add(new Vector(7,13));
        obstacle.add(new Vector(7,16));
        obstacle.add(new Vector(8,16));
        obstacle.add(new Vector(9,16));
        obstacle.add(new Vector(10,16));
        obstacle.add(new Vector(10,15));
        obstacle.add(new Vector(10,14));
        obstacle.add(new Vector(10,13));
        Map map=new Map(obstacle);
        Simulator simulator=new Simulator(map);
        Algorithm algo=new Algorithm(simulator);
        algo.explore(1000, 101);
//        algo.getMap().printMap();
        Robot robot = new Robot(true);
        ShortestPath sp = new ShortestPath(algo.getMap(), robot);
        sp.executeShortestPath(13, 18);
    }
}
