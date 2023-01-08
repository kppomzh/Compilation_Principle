package com.zhzm.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录产生式
 */
public class Cell {
    public static Cell epsilon=new Cell("ε");
    private List<Cell> cells;
    private String cellName;
    private String[] attributes;

    public Cell(String cellName){
        this.cellName=cellName;
        cells=new ArrayList<>();
    }

    public void setAttribute(String[] attributes){
        this.attributes = attributes;
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

    public String[] getAttributes() {
        return attributes;
    }
}
