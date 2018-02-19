package com.exchangeinfomanager.StockCalendar;

import java.awt.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;


@SuppressWarnings("all")
public class Meeting   {

    private LocalDate start;
    private String title;
    private String description;
    private String brief;

    private Collection<InsertedMeeting.Label> labels;

    public Meeting(String title, LocalDate start, String description, String brief2, Collection<InsertedMeeting.Label> labels) {
        this.setStart(start);
        this.setTitle(title);
        this.setDescription(description);
        this.setLocation(brief2);
        this.labels = new HashSet<>(labels);
    }

    public Collection<InsertedMeeting.Label> getLabels() {
        return this.labels;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }




    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return brief;
    }

    public void setLocation(String location) {
        this.brief = location;
    }

    @Override
    public String toString() {
        return String.format("title: %s, start: %s, end: %s, description: %s, location: %s", getTitle(), getStart(),
             getDescription(), getLocation());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Meeting meeting = (Meeting) o;

        if (!start.equals(meeting.start))
            return false;
        if (!title.equals(meeting.title))
            return false;
        if (!description.equals(meeting.description))
            return false;
        if (!brief.equals(meeting.brief))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + brief.hashCode();
        return result;
    }

    public static class Label implements Serializable {

        private Color color;
        private String name;
        private boolean active;

        public Label(String name, Color color, boolean active) {
            this.color = color;
            this.name = name;
            this.active = active;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return String.format("name: %s, color: %s", getName(), getColor());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Label label = (Label) o;

            if (active != label.active)
                return false;
            if (!color.equals(label.color))
                return false;
            if (!name.equals(label.name))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = color.hashCode();
            result = 31 * result + name.hashCode();
            result = 31 * result + (active ? 1 : 0);
            return result;
        }
    }
}
