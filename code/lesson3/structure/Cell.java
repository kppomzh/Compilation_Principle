package lesson3.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 记录产生式
 */
public class Cell {
    public static Cell epsilon=new Cell("ε");
    private List<Cell> cells;
    private String cellName;

    public Cell(String cellName){
        this.cellName=cellName;
        cells=new ArrayList<>();
    }

    public String getFirstMark() {
        if(length()!=0) {
            Cell tCell = cells.get(0);
            return tCell.getFirstMark();
        }
        else
            return getCellName();
    }


    public void addCell(Cell c){
        cells.add(c);
    }

    public List<Cell> getCell() {
        return cells;
    }


    public String getCellName() {
        return cellName;
    }

    public int length() {
        return cells.size();
    }
}
