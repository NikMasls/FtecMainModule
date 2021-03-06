package com.ftec.entities;

import com.ftec.resources.enums.TicketCategory;
import com.ftec.resources.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table
@ToString
public class Ticket {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private long userId;

    @NotNull
    @Size(min = 3, max = 100)
    private String subject;

    @NotNull
    @Size(min = 5, max = 2000)
    private String message;

    @NotNull
    private TicketStatus status = TicketStatus.NEW;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<Comment> commentList;

    private Date creationDate;

    private long supporter_id;

    private TicketCategory category;
}
