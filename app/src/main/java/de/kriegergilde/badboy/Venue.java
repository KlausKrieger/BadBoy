package de.kriegergilde.badboy;


public class Venue {

    private String id;
    private String name;
    private int distance;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Venue venue = (Venue) o;
        if (!id.equals(venue.id))
            return false;
        return !(name != null ? !name.equals(venue.name) : venue.name != null);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() { // wird von ListView verwendet!
        return name + " (" + distance + "m)";
    }
}
