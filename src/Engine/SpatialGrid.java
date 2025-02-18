package Engine;

import java.util.*;

public class SpatialGrid {
    private int cellSize;
    private Map<GridCell, List<Actor>> grid;
    
    public SpatialGrid(int cellSize) {
        this.cellSize = cellSize;
        this.grid = new HashMap<>(1000);
    }
    
    private static class GridCell {
        final int x, y;
        
        GridCell(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GridCell)) return false;
            GridCell cell = (GridCell) o;
            return x == cell.x && y == cell.y;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
    
    public void updateActorPosition(Actor actor) {
        removeActor(actor);
        addActor(actor);
    }
    
    public void addActor(Actor actor) {
        // Berechne die Zellen, die das Objekt überdeckt
        List<GridCell> cells = getCellsForActor(actor);
        for (GridCell cell : cells) {
            grid.computeIfAbsent(cell, k -> new ArrayList<>()).add(actor);
        }
    }
    
    public void removeActor(Actor actor) {
        // Entferne das Objekt aus allen Zellen, die es überdeckt
        List<GridCell> cells = getCellsForActor(actor);
        for (GridCell cell : cells) {
            List<Actor> cellActors = grid.get(cell);
            if (cellActors != null) {
                cellActors.remove(actor);
                if (cellActors.isEmpty()) {
                    grid.remove(cell);
                }
            }
        }
    }
    
    private List<GridCell> getCellsForActor(Actor actor) {
        List<GridCell> cells = new ArrayList<>();
        if (actor.getImage() == null) {
            cells.add(getCellForPosition(actor.getX(), actor.getY()));
            return cells;
        }

        // Berechne die Grenzen des Objekts
        int halfWidth = actor.getImage().getWidth() / 2;
        int halfHeight = actor.getImage().getHeight() / 2;
        int left = (actor.getX() - halfWidth) / cellSize;
        int right = (actor.getX() + halfWidth) / cellSize;
        int top = (actor.getY() - halfHeight) / cellSize;
        int bottom = (actor.getY() + halfHeight) / cellSize;

        // Füge alle überdeckten Zellen hinzu
        for (int x = left; x <= right; x++) {
            for (int y = top; y <= bottom; y++) {
                cells.add(new GridCell(x, y));
            }
        }
        return cells;
    }
    
    private GridCell getCellForPosition(int x, int y) {
        return new GridCell(x / cellSize, y / cellSize);
    }
    
    public List<Actor> getPotentialCollisions(Actor actor) {
        Set<Actor> potentialCollisions = new HashSet<>(); // Verwende Set um Duplikate zu vermeiden
        
        // Hole alle Zellen, die das Objekt überdeckt
        List<GridCell> actorCells = getCellsForActor(actor);
        
        // Überprüfe für jede Zelle auch die Nachbarzellen
        for (GridCell cell : actorCells) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    GridCell checkCell = new GridCell(cell.x + dx, cell.y + dy);
                    List<Actor> cellActors = grid.get(checkCell);
                    if (cellActors != null) {
                        potentialCollisions.addAll(cellActors);
                    }
                }
            }
        }
        
        potentialCollisions.remove(actor);
        return new ArrayList<>(potentialCollisions);
    }
} 