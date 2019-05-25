function validateRegistrationForm() {
    let form = document.forms["registration"];
    let emailRegex = /^[\w!#$%'*+\-/=?^_`{|}~]{3,15}([.][\w!#$%'*+\-/=?^_`{|}~]{3,10}){0,5}@[a-z]{2,20}([.][a-z]{2,10}){1,4}$/;
    if (!emailRegex.test(form["email"].value)) {
        document.getElementById("registration_message").innerHTML = "Illegal email";
        return false;
    }
    let loginRegex = /^\w{3,16}$/;
    if (!loginRegex.test(form["login"].value)) {
        document.getElementById("registration_message").innerHTML =
            "Illegal login. Login length should be more than 2 characters and less than 17 characters";
        return false;
    }
    let passwordRegex = /^.{6,64}$/;
    if (!passwordRegex.test(form["password"].value)) {
        document.getElementById("registration_message").innerHTML =
        "Illegal password. Password length should be more than 5 characters and less than 65 characters";
        return false;
    }
    return true;
}

function validateMessageForm() {
    let text = document.getElementById("message_text").value;
    if (text.length === 0 || text.length > 256) {
        document.getElementById("topic_action_notification").innerHTML =
            "Message text length should be between 1 and 256 characters";
        return false;
    }
    return true;
}

function validateTopicForm() {
    let title = document.forms["topic"]["title"].value;
    if (title.length === 0 || title.length > 100) {
        document.getElementById("topic_creation_message").innerHTML =
            "Could not create topic: title length should be between 1 and 100";
        return false;
    }
    let text = document.getElementById("topic_text").value;
    if (text.length === 0 || text.length > 400) {
        document.getElementById("topic_creation_message").innerHTML =
            "Could not create topic: text length should be between 1 and 400";
        return false;
    }
    return true;
}

function validateRatingAdditionForm() {
    let textFieldValue = document.forms["rating_form"]["rating"].value;
    if (textFieldValue !== null) {
        let integerRegex = /^[+-]?[0-9]+$/;
        if (integerRegex.test(textFieldValue)) {
            return true;
        }
    }
    document.getElementById("rating_change_message").innerHTML = "Could not change rating: chosen value is illegal";
    return false;
}