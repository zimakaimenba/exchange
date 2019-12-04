package com.exchangeinfomanager.TagServices;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.exchangeinfomanager.Services.TagService;
import com.exchangeinfomanager.Tag.InsertedTag;
import com.exchangeinfomanager.Tag.NodeInsertedTag;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class TagServicesForTreeChanYeLian implements TagService
{
	private CylTreeDbOperation dboptforcyltree;
	private Set<BkChanYeLianTreeNode> nodeset;
	private TagsDbOperation dboptfornode;

	public TagServicesForTreeChanYeLian (Set<BkChanYeLianTreeNode> node)
	{
		dboptforcyltree = new CylTreeDbOperation ();
		
		dboptfornode = new TagsDbOperation ();
		
		this.nodeset = node;
	}

	@Override
	public Collection<Tag> getTags() throws SQLException 
	{
		Collection<Tag> labels = new HashSet<>();
		
		for (Iterator<BkChanYeLianTreeNode> it = nodeset.iterator(); it.hasNext(); ) {
			BkChanYeLianTreeNode node = it.next();
			
			List<BkChanYeLianTreeNode> cylinfo = this.dboptforcyltree.getChanYeLianInfo (node.getMyOwnCode() , node.getType() );
			List<BkChanYeLianTreeNode> cylsliding = this.dboptforcyltree.getSlidingInChanYeLianInfo (node.getMyOwnCode() , node.getType() );
			List<BkChanYeLianTreeNode> cylchildren  = this.dboptforcyltree.getChildrenInChanYeLianInfo (node.getMyOwnCode() , node.getType() );
			
			Set<BkChanYeLianTreeNode> cylnodes = new HashSet<> ();
			cylnodes.addAll (cylinfo);
			cylnodes.addAll (cylsliding);
			cylnodes.addAll (cylchildren);
			for(BkChanYeLianTreeNode tmpnode : cylnodes) {
				if( tmpnode.getType() == BkChanYeLianTreeNode.TDXBK || tmpnode.getType() == BkChanYeLianTreeNode.TDXGG) {
					Collection<NodeInsertedTag> result = this.dboptfornode.getNodeTagsFromDataBase ( tmpnode.getMyOwnCode(), tmpnode.getType() );
					for(NodeInsertedTag tmptag : result)
						if( !this.checkDuplicate(labels, tmptag.getName() ) )
							labels.add (tmptag);
				} else
				if ( tmpnode.getType() == BkChanYeLianTreeNode.SUBGPC ) {
					if( !this.checkDuplicate(labels, tmpnode.getMyOwnName()) )
						labels.add (new InsertedTag ( new Tag (tmpnode.getMyOwnName(), null) , Integer.valueOf(tmpnode.getMyOwnCode()) ) );;
				} else
				if ( tmpnode.getType() == BkChanYeLianTreeNode.GPC ) {
						
				}
			}
		}
		
		return labels;
	}
	private Boolean checkDuplicate (Collection<Tag> labels, String chkstr)
	{
		for(Tag tmptag : labels) {
			if(tmptag.getName().equals(chkstr) )
				return true;
		}
		
		return false;
	}

	@Override
	public Tag createTag(Tag label) throws SQLException {
		return label;
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Tag> createTags(Collection<Tag> label) throws SQLException {
		return label;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteTags(Collection<Tag> label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteTag(Tag label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Tag updateTag(Tag label) throws SQLException {
		return label;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCache(TagCache cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDbOptNodesSet(Set<BkChanYeLianTreeNode> nodesets) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public  Tag combinTags(Collection<Tag> oldlabels, Tag newlabel) throws SQLException {
		return newlabel;
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Tag> updateTags(Collection<Tag> label) throws SQLException {
		return label;
		// TODO Auto-generated method stub
		
	}

	@Override
	public Tag combinTags(Tag newlabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
