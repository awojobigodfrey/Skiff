#include <queue>
#include <iostream>
#include <string>
#include "evaluated_types.h"
#include "../Core/statement.h"
#include "../Core/parser.h"
#include "../Core/utils.h"
#include "../Core/builtin.h"
#include "../Core/modes.h"

int main(int argc, char * argv[])
{
	skiff::environment::scope env;

	std::cout << "=== Parse Tree ===" << std::endl;
	std::vector<skiff::statements::statement *> test_statements =
		skiff::modes::parse_file((argc == 2 ? std::string(argv[1]) : "test.su"));

    for(statement * st : test_statements)
    {
        std::cout << st->parse_string() << std::endl;
    }
	std::cout << "=== Evaluation ===" << std::endl;
	skiff::builtin::load::load_standards(&env);

	skiff::modes::evaluate(&env, test_statements);
	std::cout << "=== Ending Scope ===" << std::endl;
	double * a = (double *) env.get_variable("a").get_raw_value();
	std::cout << env.get_debug_string() << std::endl;
#if (defined (_WIN32) || defined (_WIN64))
	system("pause");
#endif
}

