#VERTEX_CODE
precision highp float;

attribute vec4 vert_position;
attribute vec4 text_coord;
varying   mediump vec2 output_text_coord;

void main() {
    gl_Position       = vert_position;
    output_text_coord = text_coord.xy;
}
#END_CODE

#FRAGMENT_CODE
precision mediump float;

varying mediump vec2  output_text_coord;
uniform mediump float smooth_intensity;
uniform mediump float variance_tolerance;
uniform         int   face_num;

// 0: original function (refactor v1)
// 1: quadratic function
// 2: modified sigmoid function
uniform         int   variance_function;

uniform sampler2D input_img;
uniform sampler2D blur_img;
uniform sampler2D var_skin_img;
uniform sampler2D face_mask_img;

uniform float     background_attenuation;

void main()
{
    lowp vec4 orig_color = texture2D(input_img, output_text_coord).rgba;
    lowp vec3 mean_color = texture2D(blur_img,  output_text_coord).rgb;
    float variance = texture2D(var_skin_img, output_text_coord).r;  // range [0, 1]

    // skin probability and darkness attenuation
    mediump float orig_skin_prob        = texture2D(var_skin_img, output_text_coord).g;
    mediump float skin_prob             = orig_skin_prob;
    mediump float orig_dark_attenuation = clamp((mean_color.r - 0.3) * 4.0, 0.0, 1.0);
    mediump float dark_attenuation      = orig_dark_attenuation;
    mediump vec4  face_mask             = vec4(0.0, 0.0, 0.0, 0.0);
    if (face_num > 0)
    {
        face_mask              = texture2D(face_mask_img, output_text_coord).rgba;
        skin_prob              = face_mask.b > 0.005 ? min(orig_skin_prob, face_mask.r) : (orig_skin_prob * background_attenuation);
        dark_attenuation       = max(face_mask.g, orig_dark_attenuation);
    }

    // variance attenuation

    mediump float variance_attenuation;
    if (variance_function == 0)
    {
        variance_attenuation = variance_tolerance / (variance + variance_tolerance);
    }
    else if (variance_function == 1)
    {
        variance_attenuation = 0.5 + 0.5 / (1.0 + 6.0 * variance) - 0.5 * variance;
    }
    else if (variance_function == 2)
    {
        variance_attenuation = 1.0 - 0.87 * variance * variance;
    }
    else if (variance_function == 3)
    {
        variance_attenuation = 1.0 / (exp(6.0 * (variance - 0.4) + 1.0));
    }

    // blend
    mediump float final_blurred_ratio = clamp(skin_prob * variance_attenuation * dark_attenuation * smooth_intensity, 0.0, 1.0);
    mediump vec3  result_color        = mix(orig_color.rgb, mean_color, final_blurred_ratio);

    gl_FragColor = vec4(result_color, orig_color.a);

}
#END_CODE