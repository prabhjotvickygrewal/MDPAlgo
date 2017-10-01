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
        obstacle.add(new Vector(4,4));
        obstacle.add(new Vector(4,5));
        obstacle.add(new Vector(4,6));
        Map map=new Map(obstacle);
        Simulator simulator=new Simulator(map);
        Algorithm algo=new Algorithm(simulator);
        algo.explore(1000, 100);
        algo.getMap().printMap();
    }
}
