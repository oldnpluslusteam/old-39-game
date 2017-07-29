#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_refractionMap;
uniform vec2 u_targetSizeInv;
uniform float u_pxPerUnit;

const float MAX_DISTORTION_UNITS = 100.0;

void main() {
    vec4 refract1  = texture2D(u_refractionMap, v_texCoords + u_targetSizeInv * vec2(+1,+1) * 10.0);
    vec4 refract2  = texture2D(u_refractionMap, v_texCoords + u_targetSizeInv * vec2(-1,+1) * 10.0);
    vec4 refract3  = texture2D(u_refractionMap, v_texCoords + u_targetSizeInv * vec2(+1,-1) * 10.0);
    vec4 refract4  = texture2D(u_refractionMap, v_texCoords + u_targetSizeInv * vec2(-1,-1) * 10.0);

    vec2 distortion = vec2(
        refract1.r - refract2.r + refract3.r - refract4.r,
        refract1.r - refract3.r + refract2.r - refract4.r
    ) * 0.5 * 0.5;

    vec2 distortionTx = distortion * u_pxPerUnit * MAX_DISTORTION_UNITS;
    vec4 color = texture2D(u_texture, v_texCoords + distortionTx);

    gl_FragColor = color;
}
