#pragma once

/* vec3 type */
typedef struct {
    double x, y, z;
} vec3_t;


/* point3 type, alias of vector */
typedef vec3_t point3_t;


/* NOTE: all the below functions which return vectors, return NULL on error
 * or a copy of the pointer passed as res (result) if successful
 * res will be set to the result of the corresponding operation */

vec3_t *vec3_neg(vec3_t *res, const vec3_t *v);

vec3_t *vec3_add(vec3_t *res, const vec3_t *v1, const vec3_t *v2);
vec3_t *vec3_sub(vec3_t *res, const vec3_t *v1, const vec3_t *v2);
vec3_t *vec3_mul(vec3_t *res, const vec3_t *v1, const vec3_t *v2);

vec3_t *vec3_add_s(vec3_t *res, const vec3_t *v, double d);
vec3_t *vec3_sub_s(vec3_t *res, const vec3_t *v, double d);
vec3_t *vec3_mul_s(vec3_t *res, const vec3_t *v, double d);
vec3_t *vec3_div_s(vec3_t *res, const vec3_t *v, double d);

double vec3_len(const vec3_t *v);
double vec3_len_sqr(const vec3_t *v);

double dot(const vec3_t *v1, const vec3_t *v2);
vec3_t *cross(vec3_t *res, const vec3_t *v1, const vec3_t *v2);
vec3_t *vec3_unit(vec3_t *res, const vec3_t *v);

void vec3_print(const vec3_t *v);
