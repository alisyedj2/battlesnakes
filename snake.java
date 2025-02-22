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
    private boolean hazards[][];

    
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

        possibleMoves = new ArrayList<String>(Arrays.asList("up","down","left","right"));
        hazards = new boolean[11][11];

        for(Battlesnake snake : state.board().snakes()){
            for (Coord part : snake.body()){
                hazards[part.x()][part.y()] = true;
            }
        }

        avoidObstacles();

        Coord[] food = state.board().food();
        int minimumDistance = Integer.MAX_VALUE;

        int finalfoodx = 0;
        int finalfoody = 0;
        
        for(Coord foodCoord : food){
            int foodx = foodCoord.x();
            int foody = foodCoord.y();
            int distance = Math.abs(head.x() - foodx) + Math.abs(head.y() - foody);
            if(distance < minimumDistance){
                minimumDistance = distance;
                finalfoodx = foodx;
                finalfoody = foody;
            }
        }

        String direction = possibleMoves.get(0);
        
        if(finalfoodx < head.x() && possibleMoves.contains("left")) direction = "left";
        else if(finalfoodx > head.x() && possibleMoves.contains("right")) direction = "right";
        else if(finalfoody < head.y() && possibleMoves.contains("down")) direction = "down";
        else if(finalfoody > head.y() && possibleMoves.contains("up")) direction = "up";
        
        Move move = new Move(direction);
        return move;
    }

    @POST
    @Path("end")
    public void end(GameState state) {
        log.infof("%s END", state.game().id());
    }

    public void avoidObstacles(){
        if (head.x() == 0 || hazards[head.x() - 1][head.y()]){
            possibleMoves.remove("left");
        }
        if (head.x() == hazards.length-1 || hazards[head.x() + 1][head.y()]){
            possibleMoves.remove("right");
        }
        if (head.y() == 0 || hazards[head.x()][head.y() - 1]){
            possibleMoves.remove("down");
        }
        if (head.y() == hazards.length-1 || hazards[head.x()][head.y() + 1]){
            possibleMoves.remove("up");
        }
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

