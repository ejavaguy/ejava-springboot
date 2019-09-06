package info.ejava.examples.db.validation.contacts.controller;

import javax.validation.Payload;

public class ViolationStatus {
    interface BadRequest extends Payload{}
    interface UnprocessableEntity extends Payload{}
}
