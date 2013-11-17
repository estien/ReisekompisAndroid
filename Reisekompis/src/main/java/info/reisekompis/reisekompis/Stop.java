package info.reisekompis.reisekompis;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;

public class Stop {
    private int id;
    private String name;
    private String district;
    private ArrayList<Line> lines;

    @JsonCreator
    public Stop() {
    }

    public Stop(int id, String name, String district, ArrayList<Line> lines) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.lines = lines;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public String toString() {
        return name + "\r\n" + StringHelper.Join(",", lines.toArray());
    }

    public void addLine(Line line) {
        lines.add(line);
    }
}
