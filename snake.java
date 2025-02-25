///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.quarkus:quarkus-bom:${quarkus.version:2.6.2.Final}@pom
////DEPS io.quarkus:quarkus-resteasy-reactive
//DEPS io.quarkus:quarkus-resteasy-reactive-jackson
//JAVAC_OPTIONS -parameters
//JAVA 17

import com.fasterxml.jackson.databind.JsonNode;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.*;
enum Moves {
    up, down, left, right
}

@Path("/")
@ApplicationScoped
public class snake {

    @Inject
    Logger log;

    private Coord head;
    private Coord[] body;
    private List<String> possibleMoves;
    private Boolean hazards[][];
    int count;

    
    @GET
    public Info info() {
        count = 0;
        log.infof("INFO");
        return new Info("1",
                "alisyed",
                "#000000",
                "default",
                "default");
    }

    
    @POST
    @Path("/start")
    public void start(GameState state) {
        log.infof("%s START", state.game().id());
    }

    
    @POST
    @Path("/move")
    public Move move(GameState state) {
        System.out.println("move");
        head = state.you().head();
        body = state.you().body();

        hazards = new Boolean[11][11];

        for(Battlesnake snake : state.board().snakes()){
            for (Coord part : snake.body()){
                hazards[part.x()][part.y()] = true;
            }
        }
        
        for(Coord foodCoord : state.board().food()){
            int foodx = foodCoord.x();
            int foody = foodCoord.y();
            hazards[foodx][foody] = false;
        }

        String direction = bfs();
        
        Move move = new Move(direction,head.toString());
        System.out.println(move.toString());
        return move;
    }

    @POST
    @Path("end")
    public void end(GameState state) {
        log.infof("%s END", state.game().id());
    }

    private String bfs(){
        try{
        System.out.println(count++);
        Queue<Coord> queue = new LinkedList<>();
        HashMap<Coord,String> directions = new HashMap<>();
        boolean visited[][] = new boolean[11][11];
        Coord tempHead = head;
        queue.add(tempHead);
        visited[tempHead.x()][tempHead.y()] = true;
        while(queue.size() > 0){
            tempHead = queue.poll();
            if(new Boolean(false).equals(hazards[tempHead.x()][tempHead.y()])){
                return directions.get(tempHead);
            }
            if(tempHead.x() > 0 && !visited[tempHead.x()-1][tempHead.y()] && !new Boolean(true).equals(hazards[tempHead.x()-1][tempHead.y()])){
                visited[tempHead.x()-1][tempHead.y()] = true;
                Coord temp = new Coord(tempHead.x()-1,tempHead.y());
                queue.add(temp);
                directions.put(temp,directions.getOrDefault(tempHead,"left"));
            }
            if(tempHead.x() <10 && !visited[tempHead.x()+1][tempHead.y()] && !new Boolean(true).equals(hazards[tempHead.x()+1][tempHead.y()])){
                visited[tempHead.x()+1][tempHead.y()] = true;
                Coord temp = new Coord(tempHead.x()+1, tempHead.y());
                queue.add(temp);
                directions.put(temp,directions.getOrDefault(tempHead,"right"));
            }
            if(tempHead.y() >0 && !visited[tempHead.x()][tempHead.y()-1] && !new Boolean(true).equals(hazards[tempHead.x()][tempHead.y()-1])){
                visited[tempHead.x()][tempHead.y()-1] = true;
                Coord temp = new Coord(tempHead.x(), tempHead.y()-1);
                queue.add(temp);
                directions.put(temp,directions.getOrDefault(tempHead, "down"));
            }
            if(tempHead.y() < 10 && !visited[tempHead.x()][tempHead.y()+1] && !new Boolean(true).equals(hazards[tempHead.x()][tempHead.y()+1])){
                visited[tempHead.x()][tempHead.y()+1] = true;
                Coord temp = new Coord(tempHead.x(), tempHead.y()+1);
                queue.add(temp);
                directions.put(temp,directions.getOrDefault(tempHead, "up"));
            }
        }
        }
        catch(Exception e){
            System.out.println("Exception\n"+e);
        }
        return "left";
    }
}

record Info(String apiversion,
            String author,
            String color,
            String head,
            String tail) {
    public Info() {
        this("1",
                "",
                "#888888",
                "default",
                "default");
    }
}

record Move(
        String move,
        String shout
) {
    public Move(String move) { this(move, null);}
}

record GameState(
        Game game,
        int turn,
        Board board,
        Battlesnake you
) {
}

record Game(String id,
            Ruleset ruleset,
            long timeout) {
}

record Ruleset(String name,
               String version,
               Settings settings) {
}

record Settings(
        long foodSpawnChance,
        long minimumFood,
        long hazardDamagePerTurn,
        Royale royale,
        Squad squad
) {
}

record Royale(long shrinkEveryNTurns) {
}

record Squad(boolean allowBodyCollisions,
             boolean sharedElimination,
             boolean sharedHealth,
             boolean sharedLength) {
}

record Board(int height,
             int width,
             Coord[] food,
             Battlesnake[] snakes,
             Coord[] hazards) {
}

record Battlesnake(String id,
                   String name,
                   long health,
                   Coord[] body,
                   Coord head,
                   long length,
                   String latency,
                   String shout,
                   String squad) {
}

record Coord(int x, int y) {
}

