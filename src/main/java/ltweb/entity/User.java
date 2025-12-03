package ltweb.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "username", columnDefinition = "nvarchar(50) not null")
	private String username;
	
	@Column(name = "password", columnDefinition = "nvarchar(100) not null")
	private String password;
	
	@Column(name = "email", columnDefinition = "nvarchar(255)")
	private String email;
	
	@Column(name = "phone", columnDefinition = "nvarchar(15)")
	private String phone;
	
	@Column(name = "role")
	private int role;
	
	@Column(name = "active")
	private boolean active;
	
	@Column(name = "images", columnDefinition = "nvarchar(500)")
	private String images;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Category> categories;
	
}
