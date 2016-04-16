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
package uk.ac.ncl.cs.csc2024.route;

import uk.ac.ncl.cs.csc2024.busstop.BusStop;
import uk.ac.ncl.cs.csc2024.operator.Operator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Hibernate Route Entity
 *
 * Task: Create fields, methods and annotations which implicitly define an appropriate database table schema for
 * Route records.
 *
 * @author hugofirth
 * @author Modified by Dylan McKee
 *
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Route.SELECT_ALL, query = "select r from Route r order by r.number asc"),
        @NamedQuery(name = Route.SELECT_ALL_FOR_RAILWAY_STATION, query = "select distinct r from Route r where r.startStop.id =  9015 OR r.startStop.id = 9016 OR r.destinationStop.id = 9015 OR r.destinationStop.id = 9016"),
        @NamedQuery(name = Route.CUMULATIVE_FREQUENCY_BY_OK_TRAVEL, query = "select r from Route r where :operator in r.operators")
})

@Table(name = "route")
public class Route {
    public static final String SELECT_ALL = "Route.selectAll";
    public static final String SELECT_ALL_FOR_RAILWAY_STATION = "Route.selectAllForRailwayStation";
    public static final String CUMULATIVE_FREQUENCY_BY_OK_TRAVEL = "Route.cumulativeFrequencyByOkTravel";

    @Id
    @Column(name = "number")
    // Using a String type for a variable called 'number' seems extremely counter-intuitive, but the fact that
    // '16A' is in the sample data set as a 'route number' leaves me with no choice. Ugh. :-(
    private String number;

    @Column(name = "frequency")
    private int frequency;


    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "start_stop_id")
    private BusStop startStop;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "destination_stop_id")
    private BusStop destinationStop;

    @ManyToMany(
            targetEntity=Operator.class,
            cascade={CascadeType.REMOVE, CascadeType.REMOVE}
    )
    @JoinTable(
            name="operator_route",
            joinColumns=@JoinColumn(name="route_id"),
            inverseJoinColumns=@JoinColumn(name="operator_id")
    )
    private Set<Operator> operators = new HashSet<Operator>();

    public String getNumber() {
        return number;
    }

    public void setNumber(String routeNumber) {
        this.number = routeNumber;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public BusStop getStartStop() {
        return startStop;
    }

    public void setStartStop(BusStop startStop) {
        this.startStop = startStop;
    }

    public BusStop getDestinationStop() {
        return destinationStop;
    }

    public void setDestinationStop(BusStop destinationStop) {
        this.destinationStop = destinationStop;
    }

    public Set<Operator> getOperators() {
        return operators;
    }

    public void setOperators(Set<Operator> operators) {
        this.operators = operators;
    }

}
