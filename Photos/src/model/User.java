package model;

import java.io.Serializable;

/**
 * Model Class representing an user for the photos application. Each user
 * contains its name and its user role(Admin or general user).
 *
 * @author Dev Patel and Eric Chan
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
	private String userName;
	private UserRole userRole;
	
	/**
	 * Create a user with specific name and role
	 * @param userName Username
	 * @param role Userole (admin/user)
	 */
	public User(String userName, UserRole role) {
		this.userName = userName;
		this.userRole = role;
	}

	/**
	 * Get the username for the user
	 * @return username for the user
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Get the role for the user
	 * @return Admin or User role
	 */
	public UserRole getUserRole() {
		return userRole;
	}

	// overriding the hashcode and equals method on username basis so that users can be easily
	// searched in maps or lists
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
		User other = (User) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return userName;
	}
	
}
