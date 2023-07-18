#VERTEX_CODE

precision highp float;

attribute vec4 vert_position;
attribute vec4 text_coord;
varying   vec2 output_text_coord;

void main() {
    gl_Position       = vert_position;
    output_text_coord = text_coord.xy;
}

#END_CODE

#FRAGMENT_CODE

precision mediump float;

varying vec2  output_text_coord;

uniform float smooth_intensity;
uniform float variance_tolerance;
uniform int   face_num;

uniform sampler2D input_img;
uniform sampler2D blur_img;
uniform sampler2D var_img;
uniform sampler2D skin_mask_img;
uniform sampler2D face_mask_img;

void main()
{
    lowp vec4 orig_color = texture2D(input_img, output_text_coord).rgba;
    lowp vec3 mean_color = texture2D(blur_img,  output_text_coord).rgb;
    lowp vec3 var_color  = texture2D(var_img,   output_text_coord).rgb;  // range [0, 1]

    // skin probability and darkness attenuation
    float skin_prob        = texture2D(skin_mask_img, output_text_coord).r;
    float dark_attenuation = clamp((mean_color.r - 0.3) * 4.0, 0.0, 1.0);
    if (face_num > 0)
    {
        vec4 face_mask   = texture2D(face_mask_img, output_text_coord).rgba;
        skin_prob        = face_mask.b > 0.005 ? min(skin_prob, face_mask.r) : (skin_prob * face_mask.a);
        dark_attenuation = max(face_mask.g, dark_attenuation);
    }

    // variance attenuation
    float variance             = (var_color.r + var_color.g + var_color.b) / 3.0;
    float variance_attenuation = variance_tolerance / (variance + variance_tolerance);

    // blend
    float final_blurred_ratio = clamp(skin_prob * variance_attenuation * dark_attenuation * smooth_intensity, 0.0, 1.0);
    vec3  result_color        = mix(orig_color.rgb, mean_color, final_blurred_ratio);

    gl_FragColor = vec4(result_color, orig_color.a);
}

#END_CODE