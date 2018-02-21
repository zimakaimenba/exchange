package com.exchangeinfomanager.StockCalendar.view;

@SuppressWarnings("all")
public class InsertedMeeting extends Meeting {

    private int id;

    public InsertedMeeting(Meeting meeting, int id) {
        super(meeting.getTitle(), meeting.getStart(),  meeting.getDescription(), meeting.getLocation(),
            meeting.getLabels(),meeting.getSlackUrl(),meeting.getNewsownercodes());
        this.setID(id);
    }

    private void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public Meeting getMeeting() {
        return new Meeting(getTitle(), getStart(),  getDescription(), getLocation(), getLabels(),getSlackUrl(),getNewsownercodes());
    }

    public void setMeeting(Meeting meeting) {
        setTitle(meeting.getTitle());
        setStart(meeting.getStart());
        setDescription(meeting.getDescription());
        setLocation(meeting.getLocation());
        setSlackUrl(meeting.getSlackUrl());
        getLabels().clear();
        getLabels().addAll(meeting.getLabels());
    }

    public void removeMeetingSpecficOwner (String removedowner) 
    {
    	if(newsownercodes.contains(removedowner)) {
    		newsownercodes = newsownercodes.replace(removedowner + "|", "");
    	}
    }
    public void addMeetingToSpecificOwner (String newowner)
    {
    	if(!newsownercodes.contains(newowner)) {
    		newsownercodes = newsownercodes + newowner + "|";
    	}
    }
    @Override
    public String toString() {
        return String.format("id: %d, %s", getID(), getMeeting());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        InsertedMeeting that = (InsertedMeeting) o;

        if (id != that.id)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        return result;
    }

    public static class Label extends Meeting.Label {

        private int id;

        public Label(Meeting.Label label, int id) {
            super(label.getName(), label.getColor(), label.isActive());
            this.id = id;
        }

        public Meeting.Label getLabel() {
            return new Meeting.Label(getName(), getColor(), isActive());
        }

        public void setLabel(Meeting.Label label) {
            setName(label.getName());
            setColor(label.getColor());
            setActive(label.isActive());
        }

        public int getID() {
            return this.id;
        }
    }
}
