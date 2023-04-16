import vec3

def writeColor(color: vec3.Color):
    """Transforms a Color vector (type alias for Vec3) into its RGB form, provided the original Vector contains only values between 0 and 1"""
    return f"{int(255.999 * color.x())} {int(255.999 * color.y())} {int(255.999 * color.z())}\n"