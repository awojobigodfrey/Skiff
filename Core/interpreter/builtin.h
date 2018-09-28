#pragma once

#include <vector>
#include <functional>
#include <string>
#include <iostream>
#include <map>
#include "evaluated_types.h"
#include "utils.h"

namespace skiff
{
    namespace builtin
    {
        enum type
        {
            Int, Float, Double, Char, Long, String, Boolean, None, Class, Struct, Function, Any
        };

        type get_type_for(size_t id);

        size_t get_id_for(type nt);

        std::string get_name_for(type nt);

        std::string get_c_type_for(type nt);

        namespace generator
        {
            template<class T>
            environment::skiff_func_sig *create_add();

            template<class T>
            environment::skiff_func_sig *create_sub();

            template<class T>
            environment::skiff_func_sig *create_mul();

            template<class T>
            environment::skiff_func_sig *create_div();

            template<class T>
            environment::skiff_func_sig *create_mod();

            template<class T>
            environment::skiff_func_sig *create_incriment();

            template<class T>
            environment::skiff_func_sig *create_decriment();

            template<class T>
            environment::skiff_func_sig *create_equals();

            template<class T>
            environment::skiff_func_sig *create_greater_than();

            template<class T>
            environment::skiff_func_sig *create_less_than();

            template<class T>
            environment::skiff_func_sig *create_clone();

            template<class T>
            environment::skiff_func_sig *create_to_string();

            template<class T>
            environment::skiff_func_sig *create_constructor();

            namespace string_builtins
            {
                environment::skiff_object
                to_string(std::vector<environment::skiff_object> params, environment::scope *env);

                environment::skiff_object clone(std::vector<environment::skiff_object> params, environment::scope *env);
            }

            namespace function_builtins
            {
                environment::skiff_object print(std::vector<environment::skiff_object> params, environment::scope *env);
            }

            template<class T>
            inline environment::skiff_func_sig *create_add()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            return environment::skiff_object(params[0].get_value_as<T>() + params[1].get_value_as<T>(),
                                                             (environment::skiff_class *) params[2].get_value()->get_value());
                        });
            }

            template<class T>
            inline environment::skiff_func_sig *create_sub()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            return environment::skiff_object(params[0].get_value_as<T>() - params[1].get_value_as<T>(),
                                                             (environment::skiff_class *) params[2].get_value()->get_value());
                        });
            }

            template<class T>
            inline environment::skiff_func_sig *create_mul()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            return environment::skiff_object(params[0].get_value_as<T>() * params[1].get_value_as<T>(),
                                                             (environment::skiff_class *) params[2].get_value()->get_value());
                        });
            }

            template<class T>
            inline environment::skiff_func_sig *create_div()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            return environment::skiff_object(params[0].get_value_as<T>() / params[1].get_value_as<T>(),
                                                             (environment::skiff_class *) params[2].get_value()->get_value());
                        });
            }

            template<class T>
            inline environment::skiff_func_sig *create_mod()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            return environment::skiff_object(params[0].get_value_as<T>() % params[1].get_value_as<T>(),
                                                             (environment::skiff_class *) params[2].get_value()->get_value());
                        });
            }


            template<class T>
            inline environment::skiff_func_sig *create_incriment()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            return environment::skiff_object(params[0].get_value_as<T>() + 1, params[0].get_class());
                        });
            }

            template<class T>
            inline environment::skiff_func_sig *create_decriment()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            return environment::skiff_object(params[0].get_value_as<T>() - 1, params[0].get_class());
                        });
            }


            template<class T>
            inline environment::skiff_func_sig *create_equals()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            bool b = (params[0].get_value_as<T>() == params[1].get_value_as<T>());
                            return environment::skiff_object(b,
                                                             env->get_type("skiff.lang.Boolean"));
                        });
            }

            template<class T>
            inline environment::skiff_func_sig *create_greater_than()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            bool b = (params[0].get_value_as<T>() > params[1].get_value_as<T>());
                            return environment::skiff_object(b,
                                                             env->get_type("skiff.lang.Boolean"));
                        });
            }

            template<class T>
            inline environment::skiff_func_sig *create_less_than()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            bool b = (params[0].get_value_as<T>() < params[1].get_value_as<T>());
                            return environment::skiff_object(b,
                                                             env->get_type("skiff.lang.Boolean"));
                        });
            }


            template<class T>
            inline environment::skiff_func_sig *create_clone()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            return environment::skiff_object(params[0].get_value_as<T>(), params[0].get_class());
                        });
            }

            template<class T>
            inline environment::skiff_func_sig *create_to_string()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            return environment::skiff_object(std::string(std::to_string(params[0].get_value_as<T>())),
                                                             env->get_type("skiff.lang.String"));
                        });
            }

            template<class T>
            environment::skiff_func_sig *create_constructor()
            {
                return new environment::skiff_func_sig(
                        [](std::vector<environment::skiff_object> params, environment::scope *env)
                        {
                            environment::skiff_object obj = environment::skiff_object(params[0].get_class());
                            obj.set_value(new environment::skiff_value(params[0].get_value()->get_value_as<T>(),
                                                                       params[0].get_class()));
                            return obj;
                        });
            }
        }

        namespace load
        {
            template<class T>
            environment::skiff_class *
            define_native_builtins(environment::scope *env, builtin::type nt, environment::skiff_class *exnds);

            template<class T>
            environment::skiff_class *
            define_native_fixpoint_builtins(environment::scope *env, builtin::type nt, environment::skiff_class *exnds);

            environment::skiff_class *
            define_native_number_builtins(environment::scope *env, builtin::type nt, environment::skiff_class *exnds);

            environment::skiff_class *define_string_builtins(environment::scope *env, environment::skiff_class *exnds);

            environment::skiff_class *define_function_builtins(environment::scope *env, environment::skiff_class *exnds);

            void load_builtin_functions(environment::scope *env);

            void load_standards(environment::scope *env);

            template<class T>
            inline environment::skiff_class *define_native_number_builtins(environment::scope *env, builtin::type nt,
                                                                           environment::skiff_class *exnds)
            {
                environment::skiff_class *t = define_native_builtins<T>(env, nt, exnds);

                t->add_operator(environment::builtin_operation::ADD, environment::skiff_function("add", env,
                                                                                                 generator::create_add<T>()));
                t->add_operator(environment::builtin_operation::SUB, environment::skiff_function("sub", env,
                                                                                                 generator::create_sub<T>()));
                t->add_operator(environment::builtin_operation::MUL, environment::skiff_function("mul", env,
                                                                                                 generator::create_mul<T>()));
                t->add_operator(environment::builtin_operation::DIV, environment::skiff_function("div", env,
                                                                                                 generator::create_div<T>()));

                t->add_operator(environment::builtin_operation::INC, environment::skiff_function("inc", env,
                                                                                                 generator::create_incriment<T>()));
                t->add_operator(environment::builtin_operation::DEC, environment::skiff_function("dec", env,
                                                                                                 generator::create_decriment<T>()));

                return t;
            }

            template<class T>
            inline environment::skiff_class *define_native_fixpoint_builtins(environment::scope *env,
                                                                             builtin::type nt,
                                                                             environment::skiff_class *exnds)
            {
                environment::skiff_class *t = define_native_number_builtins<T>(env, nt, exnds);

                t->add_operator(environment::builtin_operation::MOD, environment::skiff_function("mod", env,
                                                                                                 generator::create_mod<T>()));

                return t;
            }

            template<class T>
            environment::skiff_class *
            define_native_builtins(environment::scope *env, builtin::type nt, environment::skiff_class *exnds)
            {
                environment::skiff_class *t = new environment::skiff_class(builtin::get_name_for(nt), exnds);

                t->add_operator(environment::builtin_operation::EQUAL, environment::skiff_function("equals", env,
                                                                                                   generator::create_equals<T>()));
                t->add_operator(environment::builtin_operation::GREATER, environment::skiff_function("gt", env,
                                                                                                     generator::create_greater_than<T>()));
                t->add_operator(environment::builtin_operation::LESS, environment::skiff_function("lt", env,
                                                                                                  generator::create_less_than<T>()));

                t->get_scope()->define_function("to_string", new environment::skiff_function("to_string", env,
                                                                                             generator::create_to_string<T>()));
                t->get_scope()->define_function("clone", new environment::skiff_function("clone", env,
                                                                                         generator::create_clone<T>()));
                t->add_constructor(environment::skiff_function("constructor", env,
                                                               generator::create_constructor<T>()));

                return t;
            }
        }

        namespace utils
        {
            environment::skiff_object *get_dominant_type(environment::skiff_object *c1, environment::skiff_object *c2);
        }
    }
}







