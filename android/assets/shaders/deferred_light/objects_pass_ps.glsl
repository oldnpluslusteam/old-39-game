#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_screenCoords;
uniform sampler2D u_texture;
uniform sampler2D u_lightTexture;

const vec3 ambient = vec3(0.1);

void main() {
    vec4 color = texture2D(u_texture, v_texCoords) * v_color;
    vec4 light = texture2D(u_lightTexture, v_screenCoords);
    gl_FragColor = color * vec4(light.xyz + ambient, 1);
}
