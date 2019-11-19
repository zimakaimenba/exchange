package com.exchangeinfomanager.labelmanagement;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting.Label;

public class DBSystemTagsService implements TagService  
{
	private TagsDbOperation db;

	public DBSystemTagsService ()
	{
		 db = new TagsDbOperation (); 
	}

	@Override
	public Collection<InsertedTag> getLabels(Set<String> nodecode) throws SQLException {
		
		Collection<InsertedTag> result = this.db.getTagsFromDataBase ( nodecode);
		return result;
	}

	@Override
	public void createLabel(Tag label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteLabel(InsertedTag label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateLabel(InsertedTag label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCache(LabelCache cache) {
		// TODO Auto-generated method stub
		
	}
	
	
}


