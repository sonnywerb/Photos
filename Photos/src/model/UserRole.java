package model;

import java.io.Serializable;

/**
 * Enum to represent a type of user. A user can ither be admin or a general
 * user.
 *
 * @author Dev Patel and Eric Chan
 */
public enum UserRole implements Serializable {
	USER, ADMIN;
}
