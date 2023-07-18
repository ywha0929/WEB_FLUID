#VERTEX_CODE
precision highp float;

attribute vec4 vert_coord;
attribute vec4 text_coord;

varying mediump vec2 frag_text_coord;

void main()
{
    gl_Position = vert_coord;
    frag_text_coord = text_coord.xy;
}
#END_CODE

#FRAGMENT_CODE
precision mediump float;

varying mediump vec2 frag_text_coord;

uniform sampler2D input_image;
uniform sampler2D blur_image;
uniform float magnify_ratio;
uniform vec3 mean_color;
uniform float skin_threshold[5];

void main()
{
    vec3 input_color = texture2D(input_image, frag_text_coord).rgb;
    vec3 blur_color  = texture2D(blur_image, frag_text_coord).rgb;

    vec3 diff_color = input_color - blur_color;
    float diff_c = (diff_color.r + diff_color.g + diff_color.b) * 0.3333333;
    diff_c = min(diff_c * diff_c * magnify_ratio, 1.0);
    
    vec3 res_color = abs(input_color - mean_color);
    float skin_prob = step(res_color.r, skin_threshold[0]) * step(res_color.g, skin_threshold[1]) * step(res_color.b, skin_threshold[2]) * step(skin_threshold[3], input_color.r - input_color.g) * step(skin_threshold[4], input_color.r - input_color.b);

    gl_FragColor = vec4(diff_c, skin_prob, 0.0, 1.0);

}
#END_CODE