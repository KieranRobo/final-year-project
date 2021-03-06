import React from 'react';
import { Alert } from 'react-bootstrap';

const SubmissionResult = props => {
    if (props.result === "COMPILATION_ERROR") {
        return (
        <div><Alert variant="dark">
            A compilation error occured with your submission.
        </Alert></div>);
    } else if (props.result === "UNKNOWN_ERROR") {
        return (
        <div><Alert variant="dark">
            An unknown error has occured. Please consult application owner.
        </Alert></div>);
    } else if (props.result === "TEST_CASES_FAILURE") {
        return (
        <Alert variant="danger">
            You're submission has failed one or more of the test cases.
        </Alert>);
    } else if (props.result === "SUCCESS") {
        return (
        <div><Alert variant="success">
            You're submission has passed all test cases. Congratulations!
        </Alert></div>);
    } else {
        return (
        <div>
            These are the test cases that will be ran against the assessed method:<br/><br/></div>);
    }
    return (<div></div>);
};

export default SubmissionResult;