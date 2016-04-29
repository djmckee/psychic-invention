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

import uk.ac.ncl.cs.csc2024.route.Route;

import javax.persistence.*;
import java.util.Set;

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
        @NamedQuery(name = BusStop.SELECT_ALL, query = BusStop.SELCT_ALL_HQL_QUERY),
        @NamedQuery(name = BusStop.SELECT_MAX_ID, query = BusStop.SELECT_MAX_ID_HQL_QUERY)

})

@Table(name = "bus_stop")
public class BusStop {

    public static final String SELECT_ALL = "BusStop.selectAll";
    public static final String SELECT_MAX_ID = "BusStop.selectMaxId";
    public static final String SELECT_MAX_ID_HQL_QUERY = "select b from BusStop b where b.id = (select max(id) from BusStop)";
    public static final String SELCT_ALL_HQL_QUERY = "select b from BusStop b order by b.id asc";

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "description")
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        BusStop busStop = (BusStop) o;

        return getId() == busStop.getId();

    }

    @Override
    public int hashCode() {
        return getId();
    }

}
