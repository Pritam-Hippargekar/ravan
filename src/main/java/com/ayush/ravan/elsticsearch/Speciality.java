package com.ayush.ravan.elsticsearch;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Speciality implements Serializable {

    @Id
    private Long id;

    private String name;
}
