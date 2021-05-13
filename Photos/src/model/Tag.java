package model;

import java.io.Serializable;

/**
 * Model Class representing a tag for a photo. Tag can be general (not owned by 
 * any user) or specifically owned by a user. Note that this class only contains the tag 
 * keys and whether tag is single valued. The values for the tags needs to be stored
 * separately into photo class.
 *
 * @author Dev Patel and Eric Chan
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // tag name
	private String tagKey;
	
	// whether the tag can have multiple values or single value
	private boolean singleValueTag;
	
	// Owner of the tag if any
	private User owner;

	/**
	 * Create a general tag without an owner.
	 * @param tagKey Tag key
	 * @param singleValueTag If tag is a single value tag
	 */
	public Tag(String tagKey, boolean singleValueTag) {
		this.tagKey = tagKey;
		this.singleValueTag = singleValueTag;
	}
	
	/**
	 * Create a tag owned by a user 
	 * @param tagKey Tag name
	 * @param singleValueTag: If tag is single valued
	 * @param owner Tag owner
	 */
	public Tag(String tagKey, boolean singleValueTag, User owner) {
		this.tagKey = tagKey;
		this.singleValueTag = singleValueTag;
		this.owner = owner;
	}

	/**
	 * Get the name of the tag
	 * @return name of the tag
	 */
	public String getTagKey() {
		return tagKey;
	}
	
	/**
	 * Get the owner for the tag, null if general tag
	 * @return owner for the tag
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * Check if the tag can have only one value
	 * @return True if single valued tag
	 */
	public boolean isSingleValueTag() {
		return singleValueTag;
	}


	// Overriding hashCode and equals on basis of the tag key, so that tags can easily be 
	// searched inside the maps and the lists
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tagKey == null) ? 0 : tagKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		if (tagKey == null) {
			if (other.tagKey != null)
				return false;
		} else if (!tagKey.equals(other.tagKey))
			return false;
		return true;
	}
	
	
}
