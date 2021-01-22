package com.aren.thewitnesspuzzle.core.rules;

import com.aren.thewitnesspuzzle.core.cursor.area.Area;
import com.aren.thewitnesspuzzle.core.graph.Tile;
import com.aren.thewitnesspuzzle.core.math.Vector2Int;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BlocksRule extends Colorable {

    public static final String NAME = "blocks";

    public boolean[][] blocks;
    public List<boolean[][]> rotatedBlocks;
    public int[] bottomLeftY; // lower cell in the leftmost column
    public final int width;
    public final int height;
    public int count;
    public final boolean rotatable;
    public final boolean subtractive;

    public BlocksRule(boolean[][] blocks, boolean rotatable, boolean subtractive) {
        this(blocks, rotatable, subtractive, Color.YELLOW);
    }

    public BlocksRule(boolean[][] blocks, boolean rotatable, boolean subtractive, Color color) {
        super(color);

        this.blocks = optimizeBlocks(blocks);
        width = blocks.length;
        height = blocks[0].length;
        this.rotatable = rotatable;
        this.subtractive = subtractive;

        precalculate();
    }

    public BlocksRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        int tempWidth = jsonObject.getInt("width");
        int tempHeight = jsonObject.getInt("height");
        String blocksStr = jsonObject.getString("blocks");

        boolean[][] tempBlocks = new boolean[tempWidth][tempHeight];
        for(int i = 0; i < tempWidth; i++){
            for(int j = 0; j < tempHeight; j++){
                tempBlocks[i][j] = blocksStr.charAt(i * tempHeight + j) == '1';
            }
        }

        blocks = optimizeBlocks(tempBlocks);
        width = blocks.length;
        height = blocks[0].length;
        rotatable = jsonObject.getBoolean("rotatable");
        subtractive = jsonObject.getBoolean("subtractive");

        precalculate();
    }

    public void precalculate() {
        rotatedBlocks = new ArrayList<>();
        bottomLeftY = new int[rotatable ? 4 : 1];
        for (int i = 0; i < (rotatable ? 4 : 1); i++) {
            boolean[][] rotated = rotateBlocks(blocks, i);
            int bly = 0;
            for (; bly < rotated[0].length; bly++) {
                if (rotated[0][bly])
                    break;
            }

            rotatedBlocks.add(rotated);
            bottomLeftY[i] = bly;
        }

        count = 0;
        for (int x = 0; x < blocks.length; x++) {
            for (int y = 0; y < blocks[0].length; y++) {
                if (blocks[x][y])
                    count++;
            }
        }
    }

    public static boolean[][] optimizeBlocks(boolean[][] blocks) {
        int mx, my, Mx, My;
        mx = my = Integer.MAX_VALUE;
        Mx = My = Integer.MIN_VALUE;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                if (blocks[i][j]) {
                    mx = Math.min(mx, i);
                    my = Math.min(my, j);
                    Mx = Math.max(Mx, i);
                    My = Math.max(My, j);
                }
            }
        }
        if (mx == Integer.MAX_VALUE) {
            return new boolean[1][1];
        }

        boolean[][] newBlocks = new boolean[Mx - mx + 1][My - my + 1];
        for (int i = mx; i <= Mx; i++) {
            for (int j = my; j <= My; j++) {
                newBlocks[i - mx][j - my] = blocks[i][j];
            }
        }
        return newBlocks;
    }

    public static boolean[][] rotateBlocks(boolean[][] blocks, int rotation) {
        int width = blocks.length;
        int height = blocks[0].length;

        boolean[][] rotated;
        if (rotation % 2 == 0) rotated = new boolean[width][height];
        else rotated = new boolean[height][width];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!blocks[i][j]) continue;

                //ccw
                if (rotation == 0) rotated[i][j] = true;
                else if (rotation == 1) rotated[height - j - 1][i] = true;
                else if (rotation == 2) rotated[width - i - 1][height - j - 1] = true;
                else rotated[j][width - i - 1] = true;
            }
        }
        return rotated;
    }

    public int getBlockCount() {
        return count;
    }

    public static boolean equalBlocks(boolean[][] a, boolean[][] b) {
        if (a.length != b.length || a[0].length != b[0].length)
            return false;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (a[i][j] != b[i][j])
                    return false;
            }
        }
        return true;
    }

    public static BlocksRule rotateRule(BlocksRule rule, int rotation) {
        return new BlocksRule(rotateBlocks(rule.blocks, rotation), rule.rotatable, rule.subtractive, rule.color);
    }

    @Override
    public boolean canValidateLocally() {
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void serialize(JSONObject jsonObject) throws JSONException {
        super.serialize(jsonObject);
        jsonObject.put("width", width);
        jsonObject.put("height", height);

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                builder.append(blocks[i][j] ? '1' : '0');
            }
        }
        jsonObject.put("blocks", builder.toString());
        jsonObject.put("rotatable", rotatable);
        jsonObject.put("subtractive", subtractive);
    }

    public static boolean tryAllSubtractiveBlocks(List<BlocksRule> subtractiveRules, List<BlocksRule> nonSubtractiveRules, int subtractiveTakenCount,
                                                  boolean[] nonSubtractiveTaken, int nonSubtractiveTakenCount, int[][] board, int[][] target) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();

        if (subtractiveRules.size() == subtractiveTakenCount) {
            return tryAllNonSubtractiveBlocks(nonSubtractiveRules, nonSubtractiveTaken, nonSubtractiveTakenCount, board, target, 0, 0);
        }

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                for (boolean[][] blocks : subtractiveRules.get(subtractiveTakenCount).rotatedBlocks) {
                    if (x + blocks.length - 1 >= board.length || y + blocks[0].length - 1 >= board[0].length)
                        continue;

                    for (int xx = 0; xx < blocks.length; xx++) {
                        for (int yy = 0; yy < blocks[0].length; yy++) {
                            if (blocks[xx][yy])
                                board[x + xx][y + yy]--;
                        }
                    }

                    if (tryAllSubtractiveBlocks(subtractiveRules, nonSubtractiveRules, subtractiveTakenCount + 1, nonSubtractiveTaken, nonSubtractiveTakenCount, board, target))
                        return true;

                    for (int xx = 0; xx < blocks.length; xx++) {
                        for (int yy = 0; yy < blocks[0].length; yy++) {
                            if (blocks[xx][yy])
                                board[x + xx][y + yy]++;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static boolean tryAllNonSubtractiveBlocks(List<BlocksRule> nonSubtractiveRules, boolean[] nonSubtractiveTaken, int nonSubtractiveTakenCount, int[][] board, int[][] target, int lx, int ly) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();

        if (nonSubtractiveRules.size() == nonSubtractiveTakenCount)
            return true;

        // Find next fill position
        while(lx < board.length) {
            if (board[lx][ly] < target[lx][ly])
                break;
            ly++;
            if (ly == board[0].length) {
                lx++;
                ly = 0;
            }
        }

        // Not happened
        if (lx >= board.length)
            return false;

        for (int i = 0; i < nonSubtractiveRules.size(); i++) {
            if (nonSubtractiveTaken[i])
                continue;

            for (int j = 0; j < nonSubtractiveRules.get(i).rotatedBlocks.size(); j++) {
                boolean[][] blocks = nonSubtractiveRules.get(i).rotatedBlocks.get(j);
                int bly = nonSubtractiveRules.get(i).bottomLeftY[j];
                if (lx + blocks.length - 1 >= board.length || ly - bly < 0 || ly + blocks[0].length - 1 - bly >= board[0].length)
                    continue;

                boolean canPlace = true;
                for (int x = 0; x < blocks.length; x++) {
                    for (int y = 0; y < blocks[0].length; y++) {
                        if (blocks[x][y] && board[lx + x][ly + y - bly] >= target[lx + x][ly + y - bly]) {
                            canPlace = false;
                            break;
                        }
                    }
                    if (!canPlace)
                        break;
                }

                if (!canPlace)
                    continue;

                for (int x = 0; x < blocks.length; x++) {
                    for (int y = 0; y < blocks[0].length; y++) {
                        if (blocks[x][y])
                            board[lx + x][ly + y - bly]++;
                    }
                }

                nonSubtractiveTaken[i] = true;
                if (tryAllNonSubtractiveBlocks(nonSubtractiveRules, nonSubtractiveTaken, nonSubtractiveTakenCount + 1, board, target, lx, ly))
                    return true;
                nonSubtractiveTaken[i] = false;

                for (int x = 0; x < blocks.length; x++) {
                    for (int y = 0; y < blocks[0].length; y++) {
                        if (blocks[x][y])
                            board[lx + x][ly + y - bly]--;
                    }
                }
            }
        }

        return false;
    }

    public static List<RuleBase> areaValidate(Area area) throws InterruptedException {
        List<BlocksRule> subtractiveBlocks = new ArrayList<>();
        List<BlocksRule> nonSubtractiveBlocks = new ArrayList<>();
        List<RuleBase> allBlocks = new ArrayList<>();
        int blockCount = 0;

        for (Tile tile : area.tiles) {
            if (tile.getRule() instanceof BlocksRule) {
                BlocksRule block = (BlocksRule) tile.getRule();
                if (block.eliminated) continue;

                if (block.subtractive) {
                    subtractiveBlocks.add(block);
                    blockCount -= block.count;
                } else {
                    nonSubtractiveBlocks.add(block);
                    blockCount += block.count;
                }
                allBlocks.add(block);
            }
        }

        // Total block count should equal to area size
        if (blockCount != 0 && blockCount != area.tiles.size()) {
            return allBlocks;
        }

        GridPuzzle gridPuzzle = (GridPuzzle) area.puzzle;
        int[][] target = new int[gridPuzzle.getWidth()][gridPuzzle.getHeight()];
        if (blockCount > 0) {
            for (Tile tile : area.tiles) {
                target[tile.getGridX()][tile.getGridY()] = 1;
            }
        }

        if (!tryAllSubtractiveBlocks(subtractiveBlocks, nonSubtractiveBlocks, 0,
                new boolean[nonSubtractiveBlocks.size()], 0, new int[gridPuzzle.getWidth()][gridPuzzle.getHeight()], target)) {
            return allBlocks;
        }

        return new ArrayList<>();
    }

    public static boolean[][] listToGridArray(List<Vector2Int> blocks) {
        if (blocks.size() == 0)
            return new boolean[1][1];

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Vector2Int v : blocks) {
            minX = Math.min(minX, v.x);
            minY = Math.min(minY, v.y);
            maxX = Math.max(maxX, v.x);
            maxY = Math.max(maxY, v.y);
        }
        boolean[][] grid = new boolean[maxX - minX + 1][maxY - minY + 1];
        for (Vector2Int v : blocks) {
            grid[v.x - minX][v.y - minY] = true;
        }
        return grid;
    }

    @Override
    public BlocksRule clone() {
        final BlocksRule obj;
        try {
            obj = (BlocksRule) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
        obj.blocks = blocks.clone();
        obj.rotatedBlocks = new ArrayList<>();
        for (boolean[][] blocks : rotatedBlocks)
            obj.rotatedBlocks.add(blocks.clone());
        obj.bottomLeftY = bottomLeftY.clone();
        return obj;
    }

}
