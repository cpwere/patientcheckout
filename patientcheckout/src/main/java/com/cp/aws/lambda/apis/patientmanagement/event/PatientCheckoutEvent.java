package com.cp.aws.lambda.apis.patientmanagement.event;

import java.io.Serializable;

public class PatientCheckoutEvent implements Serializable {

    public String firstName;
    public String middleName;
    public String lastName;
    public String ssn;

    public PatientCheckoutEvent() {
    }

    public PatientCheckoutEvent(String firstName, String middleName, String lastName, String ssn) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.ssn = ssn;
    }

    @Override
    public String toString() {

        return "PatientCheckoutEvent{" +
                "firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", ssn='" + ssn + '\'' +
                '}';
    }
}
