from uuid import UUID, uuid4


def get_uuid():
    """Returns a unique UUID string that will be used as a minion id.
    Generates a UUID string and verifies it is unique.
    Continue generating UUIDs until a unique one is generated.
    """
    return str(uuid4())


def validate_uuid(uuid_string):
    """Validates a given string as a valid UUID

    Args:
        uuid_string: The string to validate

    Returns:
        True if a valid UUID, else false
    """
    try:
        UUID(uuid_string, version=4)
        return True
    except:
        return False


def validate_length(max_length, min_length, string_type):
    def validate(s):
        if len(s) >= min_length and len(s) <= max_length:
            return s
        raise ValidationError(
            f"{string_type} must be between {max_length} and {min_length} characters long"
        )

    return validate
