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

    
    @GET
    public Info info() {
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
        log.infof("%s MOVE %s", state.game().id(), move);
        return move;
    }

    @POST
    @Path("end")
    public void end(GameState state) {
        log.infof("%s END", state.game().id());
    }

    private String bfs(){
        Queue<Coord> queue = new LinkedList<>();
        HashMap<Coord,String> directions = new HashMap<>();
        queue.add(head);
        while(queue.size() > 0){
            head = queue.poll();
            if(new Boolean(false).equals(hazards[head.x()][head.y()])){
                return directions.get(head);
            }
            if(head.x() > 0 && !new Boolean(true).equals(hazards[head.x()-1][head.y()])){
                Coord temp = new Coord(head.x()-1,head.y());
                queue.add(temp);
                directions.put(temp,directions.getOrDefault(head,"left"));
            }
            if(head.x() <10 && !new Boolean(true).equals(hazards[head.x()+1][head.y()])){
                Coord temp = new Coord(head.x()+1, head.y());
                queue.add(temp);
                directions.put(temp,directions.getOrDefault(head,"right"));
            }
            if(head.y() >0 && !new Boolean(true).equals(hazards[head.x()][head.y()-1])){
                Coord temp = new Coord(head.x(), head.y()-1);
                queue.add(temp);
                directions.put(temp,directions.getOrDefault(head, "down"));
            }
            if(head.y() < 10 && !new Boolean(true).equals(hazards[head.x()][head.y()+1])){
                Coord temp = new Coord(head.x(), head.y()+1);
                queue.add(temp);
                directions.put(temp,directions.getOrDefault(head, "up"));
            }
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

