/**
 * csc2024-hibernate-assignment
 *
 * Copyright (c) 2015 Newcastle University
 * Email: <h.firth@ncl.ac.uk/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.ac.ncl.cs.csc2024.busstop;

import javax.persistence.*;

/**
 * Hibernate BusStop Entity
 *
 * Task: Create fields, methods and annotations which implicitly define an appropriate database table schema for
 * BusStop records.
 *
 * @author hugofirth
 * @author Modified by Dylan McKee
 *
 */

@Entity
@NamedQueries({
        @NamedQuery(name = BusStop.SELECT_ALL, query = BusStop.SELECT_ALL_HQL_QUERY),
        @NamedQuery(name = BusStop.SELECT_MAX_ID, query = BusStop.SELECT_MAX_ID_HQL_QUERY)

})

@Table(name = "bus_stop")
public class BusStop {

    /**
     * The name of the 'select all bus stops' query.
     */
    public static final String SELECT_ALL = "BusStop.selectAll";

    /**
     * The name of the 'select bus stop with maximum ID' query
     */
    public static final String SELECT_MAX_ID = "BusStop.selectMaxId";

    /**
     * The HQL query to select all bus stops.
     */
    public static final String SELECT_MAX_ID_HQL_QUERY = "select b from BusStop b where b.id = (select max(id) from BusStop)";

    /**
     * The HQL query to select the bus stop with the maximum ID.
     */
    public static final String SELECT_ALL_HQL_QUERY = "select b from BusStop b order by b.id asc";

    /**
     * The primary key for the BusStop entirety.
     */
    @Id
    @Column(name = "id")
    private int id;

    /**
     * The bus stop description.
     */
    @Column(name = "description")
    private String description;

    /**
     * Returns the id of this bus stop
     * @return the id of this bus stop.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of this bus stop instance.
     * @param id the id of this bus stop instance.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns a string description of this bus stop.
     * @return the description of this bus stop instance.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this bus stop.
     * @param description the new description of this bus stop instance.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns true of object O is equal to this Bus Stop instance.
     * @param o the object to compare this Bus Stop to.
     * @return true iff 'o' is logically equal to this Bus Stop instance.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BusStop busStop = (BusStop) o;

        return getId() == busStop.getId();

    }

    /**
     * A hash value representing the unique fields of this Bus Stop instance.
     * @return in integer representing this Bus Stop uniquely.
     */
    @Override
    public int hashCode() {
        return getId();
    }

    /**
     * A human readable representation of the BusStop class.
     * @return a String describing this BusStop instance.
     */
    @Override
    public String toString() {
        return "BusStop{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }

}
