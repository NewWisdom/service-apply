import React from "react";
import PropTypes from "prop-types";
import Field from "../Field/Field";
import Label from "../Label/Label";
import TextInput from "../TextInput/TextInput";
import styles from "./BirthField.module.css";

const BirthField = ({ required }) => {
  return (
    <Field className={styles["birth-field"]}>
      <Label for="year" required={required}>
        생년월일
      </Label>
      <div className={styles.birth}>
        <TextInput
          className="year"
          id="year"
          name="year"
          type="text"
          placeholder="YYYY"
          required={required}
        />
        <TextInput
          className={styles.month}
          name="month"
          type="text"
          placeholder="MM"
          required={required}
        />
        <TextInput
          className={styles.day}
          name="day"
          type="text"
          placeholder="DD"
          required={required}
        />
      </div>
    </Field>
  );
};

BirthField.propTypes = {
  required: PropTypes.bool,
};

BirthField.defaultProps = {
  required: false,
};

export default BirthField;
