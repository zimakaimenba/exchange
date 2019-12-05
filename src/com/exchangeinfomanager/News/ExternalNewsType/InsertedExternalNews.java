package com.exchangeinfomanager.News.ExternalNewsType;

import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class InsertedExternalNews extends ExternalNewsType
{
	private int id;
	private ExternalNewsType news;

    public InsertedExternalNews(ExternalNewsType news , int id)
    {
    	super (news.getNode(), news.getTitle(), news.getStart(), news.getEnd(), news.getDescription(),
    			news.getKeyWords(), news.getLabels(), news.getNewsUrl() );
    	
    	this.news = news;
    	this.setID(id);
    }

    private void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public ExternalNewsType getNews() {
        return this.news;
    }

    public void setnews(ExternalNewsType news) {
        setTitle(news.getTitle());
        setStart(news.getStart());
        setEnd(news.getEnd());
        setDescription(news.getDescription());
        setKeyWords(news.getKeyWords());
        setNewsUrl(news.getNewsUrl());
        getLabels().clear();
        getLabels().addAll(news.getLabels());
    }

    @Override
    public String toString() {
        return String.format("id: %d, %s", getID(), getNews());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        InsertedExternalNews that = (InsertedExternalNews) o;

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

    public static class Label extends News.Label {

        private int id;

        public Label(News.Label label, int id) {
            super(label.getName(), label.getColor());
            this.id = id;
        }

        public News.Label getLabel() {
            return new News.Label(getName(), getColor());
        }

        public void setLabel(News.Label label) {
            setName(label.getName());
            setColor(label.getColor());
        }

        public int getID() {
            return this.id;
        }
    }

}
