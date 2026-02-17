package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private User leader;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ToString.Exclude 
    private List<User> members = new ArrayList<>();

    public Team(String name, User leader) {
        this.name = name;
        this.leader = leader;
    }

    public int getTeamSize() {
        return members.size();
    }

    public boolean hasMember(User user) {
        if (user == null) return false;
        return members.contains(user);
    }

    public boolean isLeader(User user) {
        if (this.leader == null || user == null) return false;
        return this.leader.equals(user);
    }

    public void addMember(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Non puoi aggiungere un utente nullo.");
        }

        if (this.members.contains(user)) {
            return;
        }
        user.assignTeam(this.id);
        this.members.add(user);
    }
    
}
