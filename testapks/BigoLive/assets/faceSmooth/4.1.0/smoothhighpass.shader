#VERTEX_CODE

precision highp float;

attribute vec4 vert_coord;
attribute vec4 text_coord;

varying vec2 frag_text_coord;

void main()
{
    gl_Position = vert_coord;
    frag_text_coord = text_coord.xy;
}

#END_CODE

#FRAGMENT_CODE

precision mediump float;

varying highp vec2 frag_text_coord;

uniform sampler2D input_image;
uniform sampler2D blur_image;

void main()
{
    vec3 input_color = texture2D(input_image, frag_text_coord).rgb;
    vec3 blur_color  = texture2D(blur_image, frag_text_coord).rgb;

    vec3 diff_color = input_color - blur_color;

    diff_color = diff_color * diff_color * 50.0;
    diff_color = min(diff_color, vec3(1.0));

    gl_FragColor = vec4(diff_color, 1.0);
}

#END_CODE