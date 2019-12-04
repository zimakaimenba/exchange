package com.exchangeinfomanager.News;



public class InsertedNews extends News {

    private int id;

    public InsertedNews(News news , int id)
    {
        super(news.getTitle(), news.getStart(),  news.getDescription(), news.getKeyWords(),
            news.getLabels(),news.getNewsUrl(),news.getNewsOwnerCodes());
        this.setID(id);
    }

    private void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public News getnews() {
        return new News(getTitle(), getStart(),  getDescription(), getKeyWords(), getLabels(),getNewsUrl(),getNewsOwnerCodes());
    }

    public void setnews(News news) {
        setTitle(news.getTitle());
        setStart(news.getStart());
        setDescription(news.getDescription());
        setKeyWords(news.getKeyWords());
        setNewsUrl(news.getNewsUrl());
        getLabels().clear();
        getLabels().addAll(news.getLabels());
    }

    @Override
    public String toString() {
        return String.format("id: %d, %s", getID(), getnews());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        InsertedNews that = (InsertedNews) o;

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
